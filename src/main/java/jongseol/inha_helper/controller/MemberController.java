package jongseol.inha_helper.controller;

import jongseol.inha_helper.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/myPage")
    public String myPage() {
        return "myPage";
    }
}