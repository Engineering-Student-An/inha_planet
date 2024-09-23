package jongseol.inha_helper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CoursemosService {


    private final RestTemplate restTemplate;

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

}
