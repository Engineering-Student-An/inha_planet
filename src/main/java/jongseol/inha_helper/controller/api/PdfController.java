//package jongseol.inha_helper.controller.api;
//
//import jongseol.inha_helper.domain.dto.QuizListDto;
//import jongseol.inha_helper.domain.dto.QuizRequestDto;
//import jongseol.inha_helper.service.OpenAIService;
//import lombok.RequiredArgsConstructor;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/quiz")
//public class PdfController {
//
//    private final OpenAIService openAIService;
//
//    @PostMapping
//    public ResponseEntity<String> createQuiz(@ModelAttribute QuizRequestDto quizRequestDto) {
//        try {
//            PDDocument document = PDDocument.load(quizRequestDto.getLectureNote().getInputStream());
//            PDFTextStripper pdfStripper = new PDFTextStripper();
//            String text = pdfStripper.getText(document);
//            document.close();
//
//            // 개행문자를 띄어쓰기로 변환
//            text = text.replaceAll("\\r?\\n", " ");
//
//            QuizListDto quiz = openAIService.quiz(quizRequestDto.getOx(), quizRequestDto.getMultipleChoice(), quizRequestDto.getShortAnswer(), text);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Error processing PDF file.";
//        }
//
//
//    }
//
//}
