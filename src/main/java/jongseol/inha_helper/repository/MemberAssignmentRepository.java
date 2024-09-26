package jongseol.inha_helper.repository;

import jongseol.inha_helper.domain.MemberAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberAssignmentRepository extends JpaRepository<MemberAssignment, Long> {

    boolean existsMemberAssignmentByAssignment_WebId(Long webId);


    boolean existsMemberAssignmentByAssignment_IdAndMember_Id(Long webId, Long memberId);

    List<MemberAssignment> findMemberAssignmentsByCompletedAndMember_Id(boolean completed, Long memberId);

    List<MemberAssignment> findMemberAssignmentsByMember_Id(Long memberId);
    MemberAssignment findMemberAssignmentByAssignment_IdAndMember_Id(Long assignId, Long memberId);

    List<MemberAssignment> findMemberAssignmentsByCompleted(boolean completed);

}
