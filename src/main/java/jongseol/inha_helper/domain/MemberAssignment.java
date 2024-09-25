package jongseol.inha_helper.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

// 학생 - 과제 사이 엔티티
public class MemberAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 과제
    @ManyToOne
    @JoinColumn(name = "assignment_web_id")
    private Assignment assignment;

    // 학생 id
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    // 완료 여부
    private boolean completed;

    public void setMember(Member member) {
        this.member = member;
        member.getMemberAssignments().add(this);
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
        assignment.getMemberAssignments().add(this);
    }

    // 완료 설정
    public void setCompleted() {
        this.completed = true;
    }
}