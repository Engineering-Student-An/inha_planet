package jongseol.inha_helper.service;

import jongseol.inha_helper.domain.dto.QuizForm;
import jongseol.inha_helper.domain.dto.QuizListDto;
import jongseol.inha_helper.domain.dto.QuizRequestDto;
import jongseol.inha_helper.exception.ConvertPdfToStringException;
import jongseol.inha_helper.exception.OpenAIServiceException;
import jongseol.inha_helper.exception.QuizOutOfRangeException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private static final String APIKEY = "";


    private final RestTemplate restTemplate;

    public QuizListDto quiz(QuizRequestDto quizRequestDto, byte[] lectureNoteData) {

        String lectureNote = parseFileToString(lectureNoteData);

        System.out.println("lectureNote = " + lectureNote);

        int ox = quizRequestDto.getOx();
        int multipleChoice = quizRequestDto.getMultipleChoice();
        int shortAnswer = quizRequestDto.getShortAnswer();

        System.out.println("ox = " + ox);
        System.out.println("multipleChoice = " + multipleChoice);
        System.out.println("shortAnswer = " + shortAnswer);
        // 문제의 총 합 > 20개 인 경우 예외 발생
        if(ox + multipleChoice + shortAnswer > 20) {
            throw new QuizOutOfRangeException("문제 개수의 총 합은 20개를 넘을 수 없습니다!");
        }


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + APIKEY);

        JSONObject messageUser = new JSONObject();
        messageUser.put("role", "user");
        messageUser.put("content", lectureNote);

        JSONObject messageSystem = new JSONObject();
        messageSystem.put("role", "system");
        messageSystem.put("content", "저는 인공지능 챗봇입니다. " +
                "사용자가 입력한 내용은 강의노트 내용이고 이를 토대로 문제를 생성합니다. " +
                "이때 ox 형태의 문제는 " + ox + "개, 객관식(4지선다) 형태의 문제는 " + multipleChoice + "개, 단답식 형태의 문제는 " + shortAnswer + "개를 생성합니다. " +
                "문제는 다음과 같은 형식을 반드시 따릅니다. " +
                "각 유형의 문제 중 처음에는 문제 유형을 적고 개행문자를 적습니다. 그리고 각 문제의 마지막에는 개행문자를 넣어서 문제별로 구분합니다. " +
                "ox 문제는 (문제번호):(문제내용) " +
                "객관식 문제는 (문제번호):(문제내용) {1.선지1번내용 / 2.선지2번내용 / 3.선지3번내용 / 4.선지4번내용} " +
                "단답식 문제는 (문제번호):(문제내용) 형식을 지킵니다." +
                "이때 문제 유형은 반드시 ox(반드시 소문자) 문제, 객관식 문재, 단답식 문제 라고 작성합니다. " +
                "*모든 문제의 답은 순서대로 나열해서 답:(모든 문제의 답을/로 구분함) 형식으로 마지막에 반환합니다. 이때 객관식의 답은 #반드시 선지 번호(ex. 1 or 2 or 3 or 4)로 반환하고# 단답식의 답은 단답식 답을 그대로 반환합니다.*" +
                "*아래의 예시와 같은 형식을 반드시 지켜서 문제와 답을 반환합니다.*\n" +
                "ox 문제\n1:부울대수는 오직 두 가지 값(0과 1)을 허용한다.\n2:OR 작업의 부울 표현은 X = A AND B이다." +
                "\n\n객관식 문제\n1:AND 연산으로 인해 출력이 HIGH가 되는 조건은? {1.모든 입력이 HIGH일 때 / 2.모든 입력이 LOW일 때 / 3.하나의 입력이 HIGH일 때 / 4.두 입력 중 하나가 LOW일 때}\n2:부울 표현 X = A + B에서 \\\"+\\\" 기호는 어떤 연산을 나타내는가? {1.곱셈 / 2.뺄셈 / 3.덧셈 / 4.OR}" +
                "\n\n단답식 문제\n1:논리 회로의 입력과 출력 간의 관계를 설명하는 표의 이름은 무엇인가?\n2:전파 지연은 시스템이 입력을 받은 후 출력을 생성하는 데 걸리는 ________를 나타낸다.\n\n답: o/x/1/4/논리관계표/시간");

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o-2024-05-13");
        requestBody.put("messages", new JSONArray(Arrays.asList(messageSystem, messageUser)));

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        String apiEndpoint = "https://api.openai.com/v1/chat/completions";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiEndpoint, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // jsonString을 JSONObject로 변환
                JSONObject obj = new JSONObject(response.getBody());

                // "choices" 배열에서 첫 번째 요소를 가져옴
                JSONObject firstChoice = obj.getJSONArray("choices").getJSONObject(0);

                // "message" 객체에서 "content" 값을 추출
                String content = firstChoice.getJSONObject("message").getString("content");

                System.out.println("content = " + content);

                return splitQuizs(content, ox, multipleChoice, shortAnswer);
            }
        } catch (Exception e) {
            throw new OpenAIServiceException(e.getMessage());
        }
        throw new OpenAIServiceException("챗gpt api 호출 중 예외가 발생했습니다!");
    }

    private String parseFileToString(byte[] lectureNoteBytes) {
        try (PDDocument document = PDDocument.load(lectureNoteBytes)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            // 개행문자를 띄어쓰기로 변환
            text = text.replaceAll("\\r?\\n", " ");

            return text;

        } catch (IOException e) {
            throw new ConvertPdfToStringException("파일을 문자열로 변환하는 과정에서 예외가 발생했습니다!");
        }
    }

    static QuizListDto splitQuizs(String content, int ox, int multipleChoice, int shortAnswer) {

        // 문제와 답을 저장할 ArrayList
        List<QuizForm> oxQuestions = new ArrayList<>();
        List<QuizForm> multipleChoiceQuestions = new ArrayList<>();
        List<QuizForm> shortAnswerQuestions = new ArrayList<>();
        List<String> answers = new ArrayList<>();

        // 문제 문자열을 문제 유형에 따라 나누기
        String[] sections = content.split("\n\n"); // 두 개의 개행으로 구분된 섹션 나누기

        // 답변 파싱
        String[] answerSections = content.split("답: ")[1].split("/");
        int sectionIndex = 0;
        int index = 0;

        // OX 문제 파싱
        if(ox > 0) {
            String[] oxQuestionsRaw = sections[sectionIndex ++].split("\n");
            for (String question : oxQuestionsRaw) {
                if (!question.equals("ox 문제") && !question.isBlank()) {
                    answers.add(answerSections[index]);
                    oxQuestions.add(new QuizForm(question, answerSections[index++]));
                }
            }
        }

        // 객관식 문제 파싱
        if(multipleChoice > 0) {
            String[] multipleChoiceQuestionsRaw = sections[sectionIndex ++].split("\n");
            for (String question : multipleChoiceQuestionsRaw) {
                if(!question.equals("객관식 문제") && !question.isBlank()) {
                    // 선지 내용 파싱
                    String[] parts = question.split("\\{");
                    String questionText = parts[0].trim(); // 질문 부분
                    String choicesText = parts[1].replace("}", "").trim(); // 선지 부분
                    List<String> choices = List.of(choicesText.split(" / ")); // 선지를 리스트로 변환
                    answers.add(answerSections[index]);
                    multipleChoiceQuestions.add(new QuizForm(questionText, answerSections[index++], choices));
                }
            }
        }

        // 단답식 문제 파싱
        if(shortAnswer > 0) {
            String[] shortAnswerQuestionsRaw = sections[sectionIndex].split("\n");
            for (String question : shortAnswerQuestionsRaw) {
                if(!question.equals("단답식 문제") && !question.isBlank()) {
                    answers.add(answerSections[index]);
                    shortAnswerQuestions.add(new QuizForm(question, answerSections[index++]));
                }
            }
        }


        return new QuizListDto(oxQuestions, multipleChoiceQuestions, shortAnswerQuestions, answers);
    }



}
