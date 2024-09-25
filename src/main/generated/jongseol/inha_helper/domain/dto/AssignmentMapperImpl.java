package jongseol.inha_helper.domain.dto;

import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import jongseol.inha_helper.domain.Assignment;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-09-25T17:06:20+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21 (Oracle Corporation)"
)
public class AssignmentMapperImpl implements AssignmentMapper {

    @Override
    public AssignmentResponseDto toResponseDto(Assignment assignment) {
        if ( assignment == null ) {
            return null;
        }

        String name = null;
        AssignmentType assignmentType = null;
        LocalDateTime deadline = null;

        name = assignment.getName();
        assignmentType = assignment.getAssignmentType();
        deadline = assignment.getDeadline();

        long dDay = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), assignment.getDeadline());
        String subjectName = null;

        AssignmentResponseDto assignmentResponseDto = new AssignmentResponseDto( subjectName, name, assignmentType, deadline, dDay );

        return assignmentResponseDto;
    }

    @Override
    public Assignment toEntity(AssignmentRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Assignment.AssignmentBuilder assignment = Assignment.builder();

        assignment.webId( dto.getWebId() );
        assignment.name( dto.getName() );
        assignment.assignmentType( dto.getAssignmentType() );
        assignment.deadline( dto.getDeadline() );

        return assignment.build();
    }
}
