package jongseol.inha_helper.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AssignmentRequestDto {
    // 과제 id
    private Long webId;

    // 과목 이름
    private String subjectName;

    // 웹강 이름
    private String name;

    // 과제 or 웹강
    private AssignmentType assignmentType;

    // 마감 기간
    private LocalDateTime deadline;
}
