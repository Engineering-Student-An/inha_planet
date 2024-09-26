package jongseol.inha_helper.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jongseol.inha_helper.domain.Assignment;
import jongseol.inha_helper.domain.Subject;
import jongseol.inha_helper.domain.dto.AssignmentRequestDto;
import jongseol.inha_helper.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AssignmentService {

    @PersistenceContext
    private final EntityManager em;

    private final AssignmentRepository assignmentRepository;
    private final SubjectService subjectService;

    public boolean notExistsByWebId(Long webId) {
        return !assignmentRepository.existsAllByWebId(webId);
    }

    public Optional<Assignment> findByWebId(Long webId) {
        return assignmentRepository.findById(webId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(AssignmentRequestDto requestDto) {
        try {
            Assignment assignment = Assignment.builder()
                    .webId(requestDto.getWebId())
                    .name(requestDto.getName())
                    .assignmentType(requestDto.getAssignmentType())
                    .deadline(requestDto.getDeadline())
                    .build();

            Subject subject = subjectService.findById(requestDto.getCourseId());
            assignment.setSubject(subject);

            assignmentRepository.save(assignment);
//            em.flush();
            em.clear();
        } catch (Exception e) {
            throw new RuntimeException("Assignment 저장 중 오류 발생", e);
        }
    }

    public List<Assignment> findByCourseId(Long courseId) {
        return assignmentRepository.findAssignmentsBySubject_Id(courseId);
    }

//    public Long saveVideo(String utoken, AssignmentRequestDto dto) {
//
//        Long videoId = dto.getWebId();
//
//        // 헤더 설정
//        HttpHeaders headers = getHttpHeaders("_ga_E323M45YWM=GS1.1.1716912481.2.0.1716912481.0.0.0; MoodleSession=464jlra7n88lpos8t8im2l0pjt; _ga=GA1.1.1505350824.1716908448");
//
//        // 바디 데이터 설정
//        String body = "utoken=" + utoken + "&modurl=https%3A//learn.inha.ac.kr/mod/vod/view.php?id%3D" + videoId;
//
//        // Document 가져옴
//        Document doc = getDocument(headers, body);
//
//        // "출석인정기간"에 해당하는 span 요소를 찾습니다.
//        Elements vodInfoElements = doc.select("div.vod_info");
//        for (Element element : vodInfoElements) {
//            Elements infoLabels = element.getElementsByClass("vod_info");
//            for (Element label : infoLabels) {
//                if (label.text().contains("출석인정기간:")) {
//                    Element valueElement = element.selectFirst(".vod_info_value");
//                    if (valueElement != null) {
//                        // DateTimeFormatter 정의 (문자열 형태에 맞춤)
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
//
//                        // 문자열을 LocalDateTime 객체로 변환
//                        LocalDateTime dateTime = LocalDateTime.parse(valueElement.text(), formatter);
//
//                        // 아직 존재하지 않을때 웹강 저장
//                        if (notExistsByWebId(videoId)) {
//                            dto.setDeadline(dateTime);
//                            save(dto);
//                            return videoId;
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    public Long saveAssign(String utoken, AssignmentRequestDto dto) {
//
//        Long assignId = dto.getWebId();
//        System.out.println("assignId = " + assignId);
//
//        // 헤더 설정
//        HttpHeaders headers = getHttpHeaders("_ga_E323M45YWM=GS1.1.1716918157.3.0.1716918157.0.0.0; MoodleSession=b9bgopakhbjc0v661qkvjtkqdb; _ga=GA1.1.1505350824.1716908448");
//
//        // 바디 데이터 설정
//        String body = "utoken=" + utoken + "&modurl=https%3A//learn.inha.ac.kr/mod/assign/view.php?id%3D" + assignId;
//
//        // Document 가져옴
//        Document doc = getDocument(headers, body);
//
//        // "종료 일시"가 포함된 행을 선택
//        Element endDateRow = doc.select("td:contains(종료 일시)").first();
//
//        if (endDateRow != null) {
//            Element endDate = endDateRow.nextElementSibling();
//            // DateTimeFormatter 정의 (문자열 형태에 맞춤)
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//
//            // 문자열을 LocalDateTime 객체로 변환
//            LocalDateTime dateTime = LocalDateTime.parse(endDate.text(), formatter);
//
//            // 아직 존재하지 않을때 과제 저장
//            if (notExistsByWebId(assignId)) {
//                dto.setDeadline(dateTime);
//                save(dto);
//                return assignId;
//            }
//        }
//        return null;
//    }
//
//    public Long saveQuiz(String utoken, AssignmentRequestDto dto) {
//
//        Long quizId = dto.getWebId();
//        System.out.println("quizId = " + quizId);
//
//        // 헤더 설정
//        HttpHeaders headers = getHttpHeaders("_ga_E323M45YWM=GS1.1.1716918157.3.0.1716918157.0.0.0; MoodleSession=b9bgopakhbjc0v661qkvjtkqdb; _ga=GA1.1.1505350824.1716908448");
//
//        // 바디 데이터 설정
//        String body = "utoken=" + utoken + "&modurl=https%3A//learn.inha.ac.kr/mod/quiz/view.php?id%3D" + quizId;
//
//        // Document 가져옴
//        Document doc = getDocument(headers, body);
//
//        // "종료 일시"가 포함된 행을 선택
//        Element endDate = doc.select("p:contains(종료일시)").getFirst();
//
//        String[] date = endDate.text().split(" ");
//        String endDateString = date[2] + " " + date[3];
//
//
//        // DateTimeFormatter 정의 (문자열 형태에 맞춤)
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//
//        // 문자열을 LocalDateTime 객체로 변환
//        LocalDateTime dateTime = LocalDateTime.parse(endDateString, formatter);
//
//        // 아직 존재하지 않을때 과제 저장
//        if (notExistsByWebId(quizId)) {
//            dto.setDeadline(dateTime);
//            save(dto);
//            return quizId;
//        }
//
//        return null;
//    }
//
//

//
//    private static HttpHeaders getHttpHeaders(String cookie) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Host", "learn.inha.ac.kr");
//        headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        headers.set("Sec-Fetch-Site", "none");
//        headers.set("Accept-Language", "ko-KR,ko;q=0.9");
//        headers.set("Sec-Fetch-Mode", "navigate");
//        headers.set("Origin", "null");
//        headers.set("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 17_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148");
//        headers.set("Connection", "keep-alive");
//        headers.set("Sec-Fetch-Dest", "document");
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.add("Cookie", cookie);
//        return headers;
//    }
//
//    private static Document getDocument(HttpHeaders headers, String body) {
//
//        // HttpEntity에 헤더와 데이터 설정
//        HttpEntity<String> entity = new HttpEntity<>(body, headers);
//
//        // RestTemplate에 HttpClient 사용 설정
//        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
//
//        // 첫 번째 요청을 실행하고, 자동으로 리다이렉트됨
//        String response = restTemplate.postForObject("https://learn.inha.ac.kr/local/coursemos/webviewapi.php?lang=ko",
//                entity, // 첫 번째 요청의 HttpEntity
//                String.class);
//
//        return (Document) Jsoup.parse(Objects.requireNonNull(response));
//    }

//    public List<Long> reloadAssignmentsInfo(String utoken, List<AssignmentRequestDto> dtos) {
//
//        List<Long> webIds = new ArrayList<>();
//
//        // 웹강, 과제, 퀴즈 세부정보 불러오기 + 저장
//        for (AssignmentRequestDto dto : dtos) {
//            if (dto.getAssignmentType().equals(AssignmentType.VIDEO)) {
//                webIds.add(saveVideo(utoken, dto));
//            } else if (dto.getAssignmentType().equals(AssignmentType.ASSIGNMENT)) {
//                webIds.add(saveAssign(utoken, dto));
//            } else if (dto.getAssignmentType().equals(AssignmentType.QUIZ)) {
//                webIds.add(saveQuiz(utoken, dto));
//            }
//        }
//
//        return webIds;
//    }
}

