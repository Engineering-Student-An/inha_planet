package jongseol.inha_helper.repository;

import jongseol.inha_helper.domain.MemberAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberAssignmentRepository extends JpaRepository<MemberAssignment, Long> {

    boolean existsMemberAssignmentByAssignment_WebId(Long webId);

    boolean existsMemberAssignmentByAssignment_WebIdAndMemberId(Long webId, Long memberId);

    List<MemberAssignment> findMemberAssignmentsByCompletedAndMemberId(boolean completed, Long memberId);

    MemberAssignment findMemberAssignmentByAssignment_WebId(Long webId);

    MemberAssignment findMemberAssignmentByAssignment_WebIdAndMemberId(Long webId, Long memberId);

    List<MemberAssignment> findMemberAssignmentsByCompleted(boolean completed);
}
