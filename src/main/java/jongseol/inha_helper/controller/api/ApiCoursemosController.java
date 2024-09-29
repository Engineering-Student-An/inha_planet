package jongseol.inha_helper.controller.api;

import jakarta.servlet.http.HttpSession;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.service.CoursemosService;
import jongseol.inha_helper.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiCoursemosController {

    private final CoursemosService coursemosService;
    private final MemberService memberService;

    @GetMapping("/coursemos/reload")
    public ResponseEntity<Map<String, String>> coursemosReload(HttpSession session, Model model) {

        Member loginMember = (Member) model.getAttribute("loginMember");

        Map<String, String> response = new HashMap<>();
        try {
            // wstoken 가져오기
            String wstoken = coursemosService.getWstoken();
            session.setAttribute("wstoken", wstoken);

            // utoken 가져오기
            String utoken = coursemosService.login(Objects.requireNonNull(loginMember).getStuId(), loginMember.getIPassword(), wstoken);
            session.setAttribute("utoken", utoken);

            // 통합
            coursemosService.reloadAll(utoken, loginMember, session);

            response.put("nextUrl", "/");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("nextUrl", "/myPage/reset/iclassInfo?error=true");
            response.put("message", "I-Class 계정 연동 중 오류가 발생했습니다!\n");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    @ModelAttribute("loginMember")
    public Member loginMember(HttpSession session, SecurityContext context) {

        if (session.getAttribute("loginMemberId") != null && !context.getAuthentication().getName().equals("anonymousUser")) {
            return memberService.findMemberById((Long) session.getAttribute("loginMemberId"));
        }
        return null;
    }
}
