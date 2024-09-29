package jongseol.inha_helper.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.dto.QuizListDto;
import jongseol.inha_helper.domain.dto.QuizRequestDto;
import jongseol.inha_helper.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

    private final MemberService memberService;

    @GetMapping
    public String home(Model model) {

        model.addAttribute("quizRequestDto", new QuizRequestDto());
        return "quiz";
    }

    @GetMapping("/loading")
    public String loading() {

        return "loadingQuiz";
    }


    @GetMapping("/list")
    public String listQuiz(HttpSession session, Model model) {

        model.addAttribute("quizList", (QuizListDto) session.getAttribute("quizList"));
        return "quizList";
    }

    @ModelAttribute("loginMember")
    public Member loginMember(HttpServletRequest request, SecurityContext context) {

        HttpSession session = request.getSession();
        if (session.getAttribute("loginMemberId") != null && !context.getAuthentication().getName().equals("anonymousUser")) {
            return memberService.findMemberById((Long) session.getAttribute("loginMemberId"));
        }
        return null;
    }
}
