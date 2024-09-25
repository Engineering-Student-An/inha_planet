package jongseol.inha_helper.service;

import jongseol.inha_helper.domain.Assignment;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.MemberAssignment;
import jongseol.inha_helper.domain.dto.AssignmentMapper;
import jongseol.inha_helper.domain.dto.AssignmentRequestDto;
import jongseol.inha_helper.domain.dto.AssignmentResponseDto;
import jongseol.inha_helper.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {


    private final AssignmentRepository assignmentRepository;
    private final MemberAssignmentService memberAssignmentService;

    public boolean existsByWebId(Long webId) {
        return assignmentRepository.existsAllAssignmentByWebId(webId);
    }

    @Transactional
    public void save(AssignmentRequestDto requestDto) {

        assignmentRepository.save(AssignmentMapper.INSTANCE.toEntity(requestDto));
    }

    public Assignment findByWebId(Long webId) {
        return assignmentRepository.findAllAssignmentByWebId(webId);
    }

    // 남은 과제 반환
    public List<AssignmentResponseDto> getRemainAssignments(Member member) {

        List<AssignmentResponseDto> remainAssignments = new ArrayList<>();
        // 과제 - 학생 에서 완료되지 않은 리스트 찾음
        List<MemberAssignment> find2 = memberAssignmentService.findByCompletedAndMemberId(false, member.getId());
        for (MemberAssignment memberAssignment : find2) {
            remainAssignments.add(AssignmentMapper.INSTANCE.toResponseDto(findByWebId(memberAssignment.getAssignment().getWebId())));
        }

        return remainAssignments;
    }


//    public Long saveAssign(String utoken, VideoLectureDTO assign) {
//
//        Long assignId = assign.getWebId();
//        System.out.println("assignId = " + assignId);
//
//        // 헤더 설정
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
//        headers.set("Content-Type", "application/x-www-form-urlencoded");
//        headers.set("Cookie", "_ga_E323M45YWM=GS1.1.1716918157.3.0.1716918157.0.0.0; MoodleSession=b9bgopakhbjc0v661qkvjtkqdb; _ga=GA1.1.1505350824.1716908448");
//
//        // 바디 데이터 설정
//        String body = "utoken="+utoken+"&modurl=https%3A//learn.inha.ac.kr/mod/assign/view.php?id%3D" + assignId;
//
//
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
//
//        Document doc = (Document) Jsoup.parse(response);
//
//        // "종료 일시"가 포함된 행을 선택
//        Element endDateRow = doc.select("td:contains(종료 일시)").first();
//
//        if(endDateRow != null) {
//            Element endDate = endDateRow.nextElementSibling();
//            // DateTimeFormatter 정의 (문자열 형태에 맞춤)
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//
//            // 문자열을 LocalDateTime 객체로 변환
//            LocalDateTime dateTime = LocalDateTime.parse(endDate.text(), formatter);
//
//            // 마감기한이 오늘보다 뒤에 있거나 아직 존재하지 않을때 AllVideoLecture 저장
//            if(!allAssignmentService.existsByWebId(assignId)) {
//                allAssignmentService.save(assign, dateTime);
//                return assignId;
//            }
//        }
//
//        return null;
//    }
}
