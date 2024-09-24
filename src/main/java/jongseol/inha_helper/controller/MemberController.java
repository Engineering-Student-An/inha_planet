package jongseol.inha_helper.controller;

import jakarta.servlet.http.HttpSession;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.dto.IclassForm;
import jongseol.inha_helper.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String resetIclassInfo(HttpSession session) {
        memberService.resetIclassInfo((Member) session.getAttribute("loginMember"), (IclassForm) session.getAttribute("iclassForm"));

        return "redirect:/myPage";
    }
}