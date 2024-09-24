package jongseol.inha_helper.controller;

import jakarta.servlet.http.HttpSession;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.dto.IclassForm;
import jongseol.inha_helper.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

    @ModelAttribute("loginMember")
    public Member loginMember(HttpSession session) {

        if (session.getAttribute("loginMemberId") != null) {
            return memberService.findMemberById((Long) session.getAttribute("loginMemberId"));
        }
        return null;
    }
}