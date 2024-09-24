package jongseol.inha_helper.api;

import jakarta.servlet.http.HttpSession;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.dto.EmailRequest;
import jongseol.inha_helper.domain.dto.IclassForm;
import jongseol.inha_helper.service.CoursemosService;
import jongseol.inha_helper.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiHomeController {

    private final EmailService emailService;
    private final CoursemosService coursemosService;

    @PostMapping("/join/email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest, HttpSession session) {

        String verifyCode = emailService.createVerifyCode();
        try {
            emailService.sendEmail(emailRequest.getEmail(), verifyCode, "email/joinEmail");
            session.setAttribute("email", emailRequest.getEmail());
            session.setAttribute("verifyCode", verifyCode);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 전송에 실패\n\n\n\n유효한 이메일 주소인지 확인하세요!");
        }

        return ResponseEntity.status(HttpStatus.OK).body("이메일 전송 완료\n\n\n\n" + emailRequest.getEmail() + "\n로 이메일을 전송했습니다.\n\n전송된 링크로 회원가입을 진행하세요!");
    }

    @PostMapping("/reset/email")
    public ResponseEntity<String> resetEmail(@RequestBody EmailRequest emailRequest, HttpSession session) {

        String verifyCode = emailService.createVerifyCode();
        try {
            emailService.sendEmail(emailRequest.getEmail(), verifyCode, "email/resetEmail");
            session.setAttribute("email", emailRequest.getEmail());
            session.setAttribute("verifyCode", verifyCode);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 전송에 실패\n\n\n\n유효한 이메일 주소인지 확인하세요!");
        }

        return ResponseEntity.status(HttpStatus.OK).body("이메일 전송 완료\n\n\n\n" + emailRequest.getEmail() + "\n로 이메일을 전송했습니다.\n\n전송된 링크로 이메일 주소 변경을 완료하세요!");
    }

    @PostMapping("/join/iclass")
    public ResponseEntity<String> loadIclassInfo(@RequestBody IclassForm iclassForm, HttpSession session) {

        try {
            // wstoken 가져오기
            String wstoken = coursemosService.getWstoken();
            session.setAttribute("wstoken", wstoken);

            // utoken 가져오기
            String utoken = coursemosService.login(iclassForm.getStuId(), iclassForm.getPassword(), wstoken);
            session.setAttribute("utoken", utoken);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("I-Class 계정 인증 실패\n\n\n\n계정 정보 확인 후 다시 입력해주세요!");
        }

        session.setAttribute("iclassForm", iclassForm);
        return ResponseEntity.status(HttpStatus.OK).body("I-Class 계정 인증 성공\n\n\n\n");
    }

    @PostMapping("/timer")
    public ResponseEntity<Void> timer(HttpSession session) {
        if (session != null) {
            // 세션 유효 시간을 30분으로 갱신
            session.setMaxInactiveInterval(1800);  // 30분
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}