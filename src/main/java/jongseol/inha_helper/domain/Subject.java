package jongseol.inha_helper.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id @Column(name = "subject_id")
    private Long id;

    // 학수번호
    private String code;

    // 과목명
    private String name;

    // 수업 시간
    @ElementCollection
    private List<String> time = new ArrayList<>();

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments = new ArrayList<>();
}
