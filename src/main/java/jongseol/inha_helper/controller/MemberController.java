package jongseol.inha_helper.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.dto.IclassForm;
import jongseol.inha_helper.domain.dto.JoinRequest;
import jongseol.inha_helper.domain.dto.PasswordRequest;
import jongseol.inha_helper.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/myPage")
    public String myPage() {
        return "member/myPage";
    }

    @GetMapping("/myPage/reset/iclassInfo")
    public String resetIclassInfo() {

        return "member/reset_iclassInfo";
    }

    @GetMapping("/myPage/reset/iclassInfo/complete")
    public String resetIclassInfo(HttpSession session, Model model) {
        memberService.resetIclassInfo((Member) Objects.requireNonNull(model.getAttribute("loginMember")), (IclassForm) session.getAttribute("iclassForm"));

        return "redirect:/myPage";
    }

    @GetMapping("/myPage/reset/email")
    public String resetEmail() {
        return "member/reset_email";
    }

    @GetMapping("/myPage/reset/email/{code}")
    public String verifyEmail(@PathVariable("code") String code, HttpSession session, Model model) {
        String verifyCode = (String) session.getAttribute("verifyCode");

        if (code.equals(verifyCode)) {
            memberService.resetEmail((Member) Objects.requireNonNull(model.getAttribute("loginMember")), (String) session.getAttribute("email"));
        }
        model.addAttribute("nextUrl", (code.equals(verifyCode)) ? "/myPage" : "/myPage/reset/email");
        model.addAttribute("errorMessage", (code.equals(verifyCode)) ? "이메일 주소 변경을 완료했습니다!" : "에러가 발생했습니다. 이메일 검증 단계로 돌아갑니다.");
        return "error/errorMessage";
    }

    @GetMapping("/myPage/reset/password")
    public String resetPassword(Model model) {

        model.addAttribute("passwordRequest", new PasswordRequest());

        return "member/reset_password";
    }

    @PostMapping("/myPage/reset/password")
    public String resetPasswordVerification(@Valid @ModelAttribute PasswordRequest passwordRequest, BindingResult bindingResult,
                HttpSession session, Model model) {
        Member loginMember = (Member) model.getAttribute("loginMember");

        if(!memberService.passwordCheck(loginMember, passwordRequest.getCurrentPassword())) {
            bindingResult.addError(new FieldError("passwordRequest",
                    "currentPassword", "현재 비밀번호와 동일하지 않습니다!"));
        }

        if (!passwordRequest.getPassword().equals(passwordRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("passwordRequest",
                    "passwordCheck", "비밀번호가 동일하지 않습니다!"));
        }

        if (bindingResult.hasErrors()) {
            return "member/reset_password";
        }

        memberService.resetPassword(loginMember, passwordRequest.getPassword());
        return "redirect:/myPage";
    }

    @ModelAttribute("loginMember")
    public Member loginMember(HttpSession session) {

        if (session.getAttribute("loginMemberId") != null) {
            return memberService.findMemberById((Long) session.getAttribute("loginMemberId"));
        }
        return null;
    }
}