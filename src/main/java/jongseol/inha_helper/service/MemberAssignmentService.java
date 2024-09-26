package jongseol.inha_helper.service;

import jongseol.inha_helper.domain.Assignment;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.MemberAssignment;
import jongseol.inha_helper.repository.MemberAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberAssignmentService {

    private final MemberAssignmentRepository memberAssignmentRepository;
    private final AssignmentService assignmentService;

    @Transactional
    public void resetMemberAssignment(Member loginMember) {
        Long memberId = loginMember.getId();
        // 로그인한 멤버의 수강 과목 리스트
        List<Long> subjects = loginMember.getSubjectList();

        // 수강 과목 반복
        for (Long courseId : subjects) {
            // 수강 과목의 과제들 모두 가져옴
            List<Assignment> assignments = assignmentService.findByCourseId(courseId);
            // 수강 과목의 모든 과제들 반복
            for (Assignment assignment : assignments) {
                // 마감기한이 오늘 이후 or
                if(!assignment.getDeadline().toLocalDate().isBefore(LocalDate.now()) && !memberAssignmentRepository.existsMemberAssignmentByAssignment_IdAndMember_Id(assignment.getId(), memberId)) {
                    save(assignment, loginMember);
                }
            }
        }

        // 수강 중인 강의 리스트에 없는 경우 삭제
        List<MemberAssignment> memberAssignmentsByMemberId = memberAssignmentRepository.findMemberAssignmentsByMember_Id(loginMember.getId());
        for (MemberAssignment memberAssignment : memberAssignmentsByMemberId) {
            // 멤버 id 로 조회한 멤버-과제 엔티티의 과목 id가 수강중인 과목 리스트에 없다면 삭제 진행
            if(!subjects.contains(memberAssignment.getAssignment().getSubject().getId())) {
                delete(memberAssignment);
            }
        }
    }

    @Transactional
    public void delete(MemberAssignment memberAssignment) {
        memberAssignmentRepository.delete(memberAssignment);
    }

    @Transactional
    public void save(Assignment assignment, Member member) {

        // 연관관계 설정
        MemberAssignment memberAssignment = new MemberAssignment();
        memberAssignment.setAssignment(assignment);
        memberAssignment.setMember(member);

        memberAssignmentRepository.save(memberAssignment);
    }

    public List<MemberAssignment> findByCompletedAndMemberId(boolean completed, Long memberId) {
        return memberAssignmentRepository.findMemberAssignmentsByCompletedAndMember_Id(completed, memberId);
    }

    public List<MemberAssignment> findByCompleted(boolean completed) {
        return memberAssignmentRepository.findMemberAssignmentsByCompleted(completed);
    }

    @Transactional
    public void setCompleted(Long webId, Long memberId) {
        memberAssignmentRepository.findMemberAssignmentByAssignment_WebIdAndMemberId(webId, memberId).setCompleted();
    }

    // 남은 과제 반환
//    public List<AssignmentResponseDto> getRemainAssignments(Member member) {
//
//        List<AssignmentResponseDto> remainAssignments = new ArrayList<>();
//        // 멤버 - 과제 에서 완료되지 않은 리스트 찾음
//        List<MemberAssignment> find2 = findByCompletedAndMemberId(false, member.getId());
//        for (MemberAssignment memberAssignment : find2) {
//            remainAssignments.add(AssignmentMapper.INSTANCE.toResponseDto(assignmentService.findByWebId(memberAssignment.getAssignment().getWebId()).orElse(null)));
//        }
//
//        return remainAssignments;
//    }
}
