package jongseol.inha_helper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.dto.Course;
import jongseol.inha_helper.domain.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CoursemosService {

    private final SubjectService subjectService;
    private final RestTemplate restTemplate;

    // 코스모스 wstoken 발급
    public String getWstoken() {
        String url = "https://api2.naddle.kr/api/v1/cos_com_school?keyword=" + "인하대학교" + "&lang=ko";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "https://api2.naddle.kr");
        headers.setConnection("keep-alive");
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.add("User-Agent", "coursemos_swift/2.2.3 (kr.coursemos.ios2; build:9995; iOS 17.4.1) Alamofire/4.9.1");
        headers.add("Accept-Language", "ko-US;q=1.0, en-US;q=0.9");
        headers.add("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJcYkNvdXJzZW1vcyIsImlhdCI6MTY5NzUyMzU1NiwiZXhwIjoyMDQ0NTkyNjI1LCJhdWQiOiJuYWRkbGUua3IiLCJzdWIiOiJyYWJiaXRAZGFsYml0c29mdC5jb20iLCJ1c2VyX2lkIjoiMSJ9.-799jl5c466FLKWoKld1PuOzfDb6FUHjauT-_XNVj0k");

        HttpEntity<String> request = new HttpEntity<>(headers);

        // Setting up the proxy
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "9494");

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        // Clear the proxy settings
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");

        // Parse the JSON response to extract the wstoken
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataNode = rootNode.path("data");
            if (dataNode.isArray() && dataNode.size() > 0) {
                JsonNode firstItem = dataNode.get(0);
                return firstItem.path("wstoken").asText();
            }
        } catch (Exception e) {
            throw new RuntimeException("I-Class 계정 연동에 실패했습니다.");
        }
        return null;
    }

    // 코스모스 로그인
    public String login(String userId, String password, String wsToken) {

        String url = "https://learn.inha.ac.kr/webservice/rest/server.php";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setConnection("keep-alive");
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        headers.add("User-Agent", "coursemos_swift/2.2.2 (kr.coursemos.ios2; build:9663; iOS 17.4.1) Alamofire/4.9.1");
        headers.add("Accept-Language", "ko-KR;q=1.0, en-KR;q=0.9");

        String body = "lang=ko&moodlewsrestformat=json&password=" + password + "&userid=" + userId + "&wsfunction=coursemos_user_login_v2&wstoken=" + wsToken;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        try {
            String jsonString = response.getBody();
            // ObjectMapper 인스턴스 생성
            ObjectMapper objectMapper = new ObjectMapper();
            // JSON 문자열을 JsonNode 객체로 변환
            JsonNode rootNode = objectMapper.readTree(jsonString);
            // "data" 객체로 이동
            JsonNode dataNode = rootNode.path("data");
            // "utoken" 값 추출
            String utoken = dataNode.get("utoken").textValue();

            return utoken;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    // 과목 id 추출
    @Transactional
    public List<Long> getCourseIds(Member loginMember, HttpSession session) {
        String url = "https://learn.inha.ac.kr/webservice/rest/server.php";

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Host", "learn.inha.ac.kr");
        headers.set("Cookie", "_ga_E323M45YWM=GS1.1.1715901782.18.0.1715901782.0.0.0; _ga=GA1.1.33679238.1714311933");
        headers.set("Connection", "keep-alive");
        headers.set("Accept", "*/*");
        headers.set("User-Agent", "coursemos_swift/2.2.3 (kr.coursemos.ios2; build:9995; iOS 17.4.1) Alamofire/4.9.1");
        headers.setAcceptLanguageAsLocales(Collections.singletonList(Locale.forLanguageTag("ko-KR")));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("lang", "ko");
        map.add("moodlewsrestformat", "json");
        map.add("wsfunction", "coursemos_course_get_mycourses_v2");
        map.add("wstoken", (String) session.getAttribute("utoken"));

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        String jsonString = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        List<Long> ids = new ArrayList<>();
        try {
            Response response1 = mapper.readValue(jsonString, Response.class);
            for (Course course : response1.getData()) {
                if (course.getCu_visible() == 1 && !course.getDay_cd().isEmpty()) {
                    ids.add(course.getId());

                    // 존재하지 않는 과목은 저장
                    if(!subjectService.existsById(course.getId())) {
                        subjectService.save(course.getId(), course.getIdnumber(), course.getFullname(), course.getDay_cd(), course.getHour1());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 수강 중 과목 리스트가 다르면 재설정
        if(!loginMember.getSubjectList().equals(ids)) {
            loginMember.setSubjectList(ids);
        }

        // 학생의 수강 전체 리스트 (courseId 리스트) 반환
        return ids;
    }
}
