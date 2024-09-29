package jongseol.inha_helper.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.domain.MemberAssignment;
import jongseol.inha_helper.domain.dto.IclassForm;
import jongseol.inha_helper.domain.dto.JoinRequest;
import jongseol.inha_helper.domain.dto.LoginRequest;
import jongseol.inha_helper.domain.dto.RemainingAssignmentDto;
import jongseol.inha_helper.service.CoursemosService;
import jongseol.inha_helper.service.MemberAssignmentService;
import jongseol.inha_helper.service.MemberService;
import jongseol.inha_helper.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;
    private final SubjectService subjectService;
    private final MemberAssignmentService memberAssignmentService;
    private final CoursemosService coursemosService;

    @GetMapping("/")
    public String home(Model model) {

        Member loginMember = (Member) model.getAttribute("loginMember");

        if(loginMember != null) {
            // 오늘의 강의를 모델에 추가
            model.addAttribute("todaySubjects", subjectService.getTodaySubjects(loginMember));

            memberAssignmentService.resetMemberAssignment(loginMember);
            // 남은 과제를 모델에 추가
            List<RemainingAssignmentDto> remainingAssignmentDtos = new ArrayList<>();

            List<MemberAssignment> memberAssignments = memberAssignmentService.findByCompletedAndMemberId(false, loginMember.getId());
            for (MemberAssignment memberAssignment : memberAssignments) {
                remainingAssignmentDtos.add(RemainingAssignmentDto.builder()
                        .memberAssignmentId(memberAssignment.getId())
                        .name(memberAssignment.getAssignment().getName())
                        .subjectName(memberAssignment.getAssignment().getSubject().getName())
                        .assignmentType(memberAssignment.getAssignment().getAssignmentType().getDisplayName())
                        .remainingSeconds(Duration.between(LocalDateTime.now(), memberAssignment.getAssignment().getDeadline()).getSeconds())
                        .build());
            }
            model.addAttribute("remainAssignments", remainingAssignmentDtos);

        }


        return "home";
    }


    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());

        return "login";
    }

    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("joinRequest", new JoinRequest());

        return "join/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult,
                       HttpSession session) {
        if (memberService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest",
                    "loginId", "이미 가입된 ID입니다!"));
        }

        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest",
                    "passwordCheck", "비밀번호가 동일하지 않습니다!"));
        }

        if (bindingResult.hasErrors()) {
            return "join/join";
        }
        session.setAttribute("joinRequest", joinRequest);
        return "redirect:/join/email";
    }

    @GetMapping("/join/email")
    public String email() {

        return "join/verifyEmail";
    }

    @GetMapping("/join/email/{code}")
    public String verifyEmail(@PathVariable("code") String code, HttpSession session, Model model) {
        String verifyCode = (String) session.getAttribute("verifyCode");

        if (code.equals(verifyCode)) {
            return "redirect:/join/iclassInfo";
        }

        model.addAttribute("nextUrl", "/join/email");
        model.addAttribute("errorMessage", "에러가 발생했습니다. 이메일 검증 단계로 돌아갑니다.");
        return "error/errorMessage";
    }

    @GetMapping("/join/iclassInfo")
    public String iclassInfo(Model model) {

        return "join/iclassInfo";
    }

    @GetMapping("/join/complete")
    public String joinComplete(HttpSession session, HttpServletRequest request, Model model) {

        memberService.join((JoinRequest) session.getAttribute("joinRequest"),
                (String) request.getSession().getAttribute("email"),
                (IclassForm) session.getAttribute("iclassForm"));

        model.addAttribute("errorMessage", "회원가입을 완료했습니다!\n로그인 페이지로 이동합니다.");
        model.addAttribute("nextUrl", "/login");
        return "error/errorMessage";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) {

        model.addAttribute("errorMessage", "일치하는 회원 정보가 없습니다.\n로그인 정보를 확인해주세요.");
        model.addAttribute("nextUrl", "/login");

        return "error/errorMessage";
    }

    @GetMapping("/coursemos/reload")
    public String afterLogin() {
        return "loading";
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
