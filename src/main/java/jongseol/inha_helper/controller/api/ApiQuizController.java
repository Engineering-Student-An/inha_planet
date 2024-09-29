package jongseol.inha_helper.controller.api;

import jakarta.servlet.http.HttpSession;
import jongseol.inha_helper.domain.dto.QuizListDto;
import jongseol.inha_helper.domain.dto.QuizRequestDto;
import jongseol.inha_helper.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiQuizController {

    private final OpenAIService openAIService;

    @PostMapping("/quiz")
    public ResponseEntity<?> createQuiz(@ModelAttribute QuizRequestDto quizRequestDto, HttpSession session) {

        try {
            QuizListDto quizListDto = openAIService.quiz(quizRequestDto);
            session.setAttribute("quizList", quizListDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("퀴즈를 성공적으로 생성했습니다.");
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("퀴즈 생성 중 오류가 발생했습니다.");
        }
    }
}
