//package jongseol.inha_helper.domain.dto;
//
//import jongseol.inha_helper.domain.Assignment;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;
//
//@Mapper
//public interface AssignmentMapper {
//
//    AssignmentMapper INSTANCE = Mappers.getMapper(AssignmentMapper.class);
//
//    @Mapping(target = "dDay", expression = "java(java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), assignment.getDeadline()))")
//    AssignmentResponseDto toResponseDto(Assignment assignment);
//
//    @Mapping(target = "subject", ignore = true) // subject 필드 매핑 제외
//    Assignment toEntity(AssignmentRequestDto dto);
//}
