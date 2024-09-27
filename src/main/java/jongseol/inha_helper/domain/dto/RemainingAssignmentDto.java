package jongseol.inha_helper.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RemainingAssignmentDto {

    private Long memberAssignmentId;
    private String name;
    private String subjectName;
    private String assignmentType;
    private Long remainingSeconds;
}
