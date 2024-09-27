package jongseol.inha_helper.domain;

import jakarta.persistence.*;
import jongseol.inha_helper.domain.dto.AssignmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Assignment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long id;

    // 과제 고유 번호
    private Long webId;

    // 과목 이름
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    // 과제 이름
    private String name;

    // 과제 or 웹강
    @Enumerated(value = EnumType.STRING)
    private AssignmentType assignmentType;

    // 마감 기간
    private LocalDateTime deadline;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberAssignment> memberAssignments = new ArrayList<>();

    public void setSubject(Subject subject) {
        this.subject = subject;
        subject.getAssignments().add(this);
    }

    public Assignment (String name, LocalDateTime deadline) {
        this.name = name;
        this.deadline = deadline;
    }
}
