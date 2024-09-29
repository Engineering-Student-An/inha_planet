package jongseol.inha_helper.domain.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class QuizRequestDto {
    int ox;
    int multipleChoice;
    int shortAnswer;

    private MultipartFile lectureNote;
}
