package jongseol.inha_helper.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String loginId;
    private String name;
    private String password;


    // I-Class 계정 학번
    private String stuId;
    // I-Class 계정 비밀번호
    private String iPassword;

    @Setter
    private String email;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberAssignment> memberAssignments = new ArrayList<>();


    // 수강 과목 리스트
    @Setter
    @ElementCollection
    private List<Long> subjectList = new ArrayList<>();

    // I-Class 정보 설정
    public void setIclassInfo(String stuId, String iPassword) {
        this.stuId = stuId;
        this.iPassword = iPassword;
    }

}
