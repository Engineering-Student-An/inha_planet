package jongseol.inha_helper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.dto.AssignmentRequestDto;
import jongseol.inha_helper.domain.dto.AssignmentType;
import jongseol.inha_helper.domain.dto.Course;
import jongseol.inha_helper.domain.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CoursemosService {

    private final SubjectService subjectService;
    private final MemberAssignmentService memberAssignmentService;
    private final AssignmentService assignmentService;
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
                    if (!subjectService.existsById(course.getId())) {
                        subjectService.save(course.getId(), course.getIdnumber(), course.getFullname(), course.getDay_cd(), course.getHour1());
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // 수강 중 과목 리스트가 다르면 재설정
        if (!loginMember.getSubjectList().equals(ids)) {
            loginMember.setSubjectList(ids);
        }

        // 학생의 수강 전체 리스트 (courseId 리스트) 반환
        return ids;
    }

    // 웹강과 과제 리스트 가져오기
    public List<AssignmentRequestDto> getList(String utoken, Long courseId) {

        try {

            // ========================== 각 과목 페이지 입장 ============================

            // URL 설정
            String url = "https://learn.inha.ac.kr/webservice/rest/server.php";

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Host", "learn.inha.ac.kr");
            headers.set("Connection", "keep-alive");
            headers.set("Accept", "*/*");
            headers.set("User-Agent", "coursemos_swift/2.2.3 (kr.coursemos.ios2; build:9995; iOS 17.4.1) Alamofire/4.9.1");
            headers.set("Accept-Language", "ko-KR;q=1.0, io-KR;q=0.9");
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            headers.set("Cookie", "_ga_E323M45YWM=GS1.1.1716901219.1.1.1716901326.0.0.0; _ga=GA1.1.833759194.1716901220; MoodleSession=8s9fovhei8rtj145u1rb82ma3m; ubboard_read=%25AA%25A5ej%25C8%2593%25F6%25BEa%250B%2500i%25BA%2596");

            // 바디 설정
            String body = "courseid=" + courseId + "&lang=ko&moodlewsrestformat=json&wsfunction=coursemos_course_get_contents_v2&wstoken=" + utoken;

            // HttpEntity 생성
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            // RestTemplate 생성
            RestTemplate restTemplate = new RestTemplate();

            // 요청 실행
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // ========================== 각 페이지에서 웹강, 과제, 퀴즈 찾음 =========================
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                // JSON 문자열을 JsonNode로 파싱
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataArray = rootNode.get("data");

                // 결과를 저장할 리스트
                List<AssignmentRequestDto> assignments = new ArrayList<>();

                // 모든 웹강, 과제, 퀴즈 추출
                if (dataArray.isArray()) {
                    for (JsonNode section : dataArray) {
                        JsonNode modulesArray = section.get("modules");
                        if (modulesArray != null && modulesArray.isArray()) {
                            for (JsonNode module : modulesArray) {
                                Long id = module.get("id").asLong();
                                if (assignmentService.notExistsByWebId(id)) {
                                    AssignmentRequestDto dto = AssignmentRequestDto.builder().webId(id).courseId(courseId).name(module.get("name").asText()).build();

                                    String modname = module.get("modname").asText();
                                    if ("vod".equals(modname)) {
                                        dto.setAssignmentType(AssignmentType.VIDEO);
                                    } else if ("assign".equals(modname)) {
                                        dto.setAssignmentType(AssignmentType.ASSIGNMENT);
                                    } else if ("quiz".equals(modname)) {
                                        dto.setAssignmentType(AssignmentType.QUIZ);
                                    } else {
                                        break;
                                    }

                                    assignments.add(dto);
                                }
                            }
                        }
                    }
                }

                return assignments;

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Long saveVideo(String utoken, AssignmentRequestDto dto) {

        Long videoId = dto.getWebId();

        // 헤더 설정
        HttpHeaders headers = getHttpHeaders("_ga_E323M45YWM=GS1.1.1716912481.2.0.1716912481.0.0.0; MoodleSession=464jlra7n88lpos8t8im2l0pjt; _ga=GA1.1.1505350824.1716908448");

        // 바디 데이터 설정
        String body = "utoken=" + utoken + "&modurl=https%3A//learn.inha.ac.kr/mod/vod/view.php?id%3D" + videoId;

        // Document 가져옴
        Document doc = getDocument(headers, body);

//         "출석인정기간"에 해당하는 span 요소를 찾습니다.
        Elements vodInfoElements = doc.select("div.vod_info");
        for (Element element : vodInfoElements) {
            Elements infoLabels = element.getElementsByClass("vod_info");
            for (Element label : infoLabels) {
                if (label.text().contains("출석인정기간:")) {
                    Element valueElement = element.selectFirst(".vod_info_value");
                    if (valueElement != null) {
                        // DateTimeFormatter 정의 (문자열 형태에 맞춤)
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

                        // 문자열을 LocalDateTime 객체로 변환
                        LocalDateTime dateTime = LocalDateTime.parse(valueElement.text(), formatter);

                        // 아직 존재하지 않을때 웹강 저장
                        if (assignmentService.notExistsByWebId(videoId)) {
                            dto.setDeadline(dateTime);
                            assignmentService.save(dto);
                            return videoId;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Long saveAssign(String utoken, AssignmentRequestDto dto) {

        Long assignId = dto.getWebId();
        System.out.println("assignId = " + assignId);

        // 헤더 설정
        HttpHeaders headers = getHttpHeaders("_ga_E323M45YWM=GS1.1.1716918157.3.0.1716918157.0.0.0; MoodleSession=b9bgopakhbjc0v661qkvjtkqdb; _ga=GA1.1.1505350824.1716908448");

        // 바디 데이터 설정
        String body = "utoken=" + utoken + "&modurl=https%3A//learn.inha.ac.kr/mod/assign/view.php?id%3D" + assignId;

        // Document 가져옴
        Document doc = getDocument(headers, body);

        // "종료 일시"가 포함된 행을 선택
        Element endDateRow = doc.select("td:contains(종료 일시)").first();

        if (endDateRow != null) {
            Element endDate = endDateRow.nextElementSibling();
            // DateTimeFormatter 정의 (문자열 형태에 맞춤)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // 문자열을 LocalDateTime 객체로 변환
            LocalDateTime dateTime = LocalDateTime.parse(endDate.text(), formatter);

            // 아직 존재하지 않을때 과제 저장
            if (assignmentService.notExistsByWebId(assignId)) {
                dto.setDeadline(dateTime);
                assignmentService.save(dto);
                return assignId;
            }
        }
        return null;
    }

    public Long saveQuiz(String utoken, AssignmentRequestDto dto) {

        Long quizId = dto.getWebId();
        System.out.println("quizId = " + quizId);

        // 헤더 설정
        HttpHeaders headers = getHttpHeaders("_ga_E323M45YWM=GS1.1.1716918157.3.0.1716918157.0.0.0; MoodleSession=b9bgopakhbjc0v661qkvjtkqdb; _ga=GA1.1.1505350824.1716908448");

        // 바디 데이터 설정
        String body = "utoken=" + utoken + "&modurl=https%3A//learn.inha.ac.kr/mod/quiz/view.php?id%3D" + quizId;

        // Document 가져옴
        Document doc = getDocument(headers, body);

        // "종료 일시"가 포함된 행을 선택
        Element endDate = doc.select("p:contains(종료일시)").getFirst();

        String[] date = endDate.text().split(" ");
        String endDateString = date[2] + " " + date[3];


        // DateTimeFormatter 정의 (문자열 형태에 맞춤)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 문자열을 LocalDateTime 객체로 변환
        LocalDateTime dateTime = LocalDateTime.parse(endDateString, formatter);

        // 아직 존재하지 않을때 과제 저장
        if (assignmentService.notExistsByWebId(quizId)) {
            dto.setDeadline(dateTime);
            assignmentService.save(dto);
            return quizId;
        }

        return null;
    }


    private static HttpHeaders getHttpHeaders(String cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "learn.inha.ac.kr");
        headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.set("Sec-Fetch-Site", "none");
        headers.set("Accept-Language", "ko-KR,ko;q=0.9");
        headers.set("Sec-Fetch-Mode", "navigate");
        headers.set("Origin", "null");
        headers.set("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 17_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148");
        headers.set("Connection", "keep-alive");
        headers.set("Sec-Fetch-Dest", "document");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Cookie", cookie);
        return headers;
    }

    private static Document getDocument(HttpHeaders headers, String body) {

        // HttpEntity에 헤더와 데이터 설정
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // RestTemplate에 HttpClient 사용 설정
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        // 첫 번째 요청을 실행하고, 자동으로 리다이렉트됨
        String response = restTemplate.postForObject("https://learn.inha.ac.kr/local/coursemos/webviewapi.php?lang=ko",
                entity, // 첫 번째 요청의 HttpEntity
                String.class);

        return (Document) Jsoup.parse(Objects.requireNonNull(response));
    }

    public void reloadAll(String utoken, Member loginMember, HttpSession session) {
        List<Long> courseIds = getCourseIds(loginMember, session);

        List<AssignmentRequestDto> dtos = new ArrayList<>();
        // 과목 반복
        for (Long courseId : courseIds) {
            // 웹강, 과제, 퀴즈 dto 추출
            dtos.addAll(getList(utoken, courseId));
        }

        // 웹강, 과제, 퀴즈 세부정보 불러오기 + 저장
        for (AssignmentRequestDto dto : dtos) {
            if (dto.getAssignmentType().equals(AssignmentType.VIDEO)) {
                saveVideo(utoken, dto);
            } else if (dto.getAssignmentType().equals(AssignmentType.ASSIGNMENT)) {
                saveAssign(utoken, dto);
            } else if (dto.getAssignmentType().equals(AssignmentType.QUIZ)) {
                saveQuiz(utoken, dto);
            }
        }

    }

}





