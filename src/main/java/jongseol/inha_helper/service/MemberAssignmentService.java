package jongseol.inha_helper.service;

import jakarta.transaction.Transactional;
import jongseol.inha_helper.domain.MemberAssignment;
import jongseol.inha_helper.repository.MemberAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberAssignmentService {

    private final MemberAssignmentRepository memberAssignmentRepository;


//    @Transactional
//    public void save(Long webId, Long memberId) {
//        MemberAssignment memberAssignment = MemberAssignment.builder()
//                .memberId(memberId).completed(false).build();
//        memberAssignmentRepository.save(memberAssignment);
//    }

    public boolean existsByWebIdAndMemberId(Long webId, Long memberId) {
        return memberAssignmentRepository.existsMemberAssignmentByAssignment_WebIdAndMemberId(webId, memberId);
    }

    public List<MemberAssignment> findByCompletedAndMemberId(boolean completed, Long memberId) {
        return memberAssignmentRepository.findMemberAssignmentsByCompletedAndMemberId(completed, memberId);
    }

    public List<MemberAssignment> findByCompleted(boolean completed) {
        return memberAssignmentRepository.findMemberAssignmentsByCompleted(completed);
    }

    @Transactional
    public void setCompleted(Long webId, Long memberId) {
        memberAssignmentRepository.findMemberAssignmentByAssignment_WebIdAndMemberId(webId, memberId).setCompleted();
    }
}
