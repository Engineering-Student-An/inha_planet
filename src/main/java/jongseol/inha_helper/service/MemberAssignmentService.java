package jongseol.inha_helper.service;

import jongseol.inha_helper.domain.Assignment;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.MemberAssignment;
import jongseol.inha_helper.domain.dto.RemainingAssignmentDto;
import jongseol.inha_helper.repository.MemberAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberAssignmentService {

    private final MemberAssignmentRepository memberAssignmentRepository;
    private final AssignmentService assignmentService;

    public List<RemainingAssignmentDto> getRemainingAssignmentDtos(Member loginMember) {
        List<RemainingAssignmentDto> remainingAssignmentDtos = new ArrayList<>();

        List<MemberAssignment> memberAssignments = findByCompletedAndMemberId(false, loginMember.getId());
        for (MemberAssignment memberAssignment : memberAssignments) {
            remainingAssignmentDtos.add(RemainingAssignmentDto.builder()
                    .memberAssignmentId(memberAssignment.getId())
                    .name(memberAssignment.getAssignment().getName())
                    .subjectName(memberAssignment.getAssignment().getSubject().getName())
                    .assignmentType(memberAssignment.getAssignment().getAssignmentType().getDisplayName())
                    .remainingSeconds(Duration.between(LocalDateTime.now(), memberAssignment.getAssignment().getDeadline()).getSeconds())
                    .build());
        }

        return remainingAssignmentDtos;
    }

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

    @Transactional
    public void setCompleted(Long memberAssignmentId) {

        Optional<MemberAssignment> memberAssignment = memberAssignmentRepository.findById(memberAssignmentId);

        if(memberAssignment.isEmpty()) {
            throw new RuntimeException("완료 처리 도중 에러가 발생했습니다!\n다시 시도해주세요.");
        }
        memberAssignment.get().setCompleted();
    }

}
