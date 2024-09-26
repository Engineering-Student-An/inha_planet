package jongseol.inha_helper.api;

import jakarta.servlet.http.HttpSession;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.service.MemberAssignmentService;
import jongseol.inha_helper.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiMemberAssignmentController {

    private final MemberAssignmentService memberAssignmentService;
    private final MemberService memberService;

    @PostMapping("/assignment/{assignId}/complete")
    public ResponseEntity<String> completeAssignment(@PathVariable("assignId") Long memberAssignId) {

        try {
            memberAssignmentService.setCompleted(memberAssignId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok("완료했습니다!");
    }

    @ModelAttribute("loginMember")
    public Member loginMember(HttpSession session, SecurityContext context) {

        if (session.getAttribute("loginMemberId") != null && !context.getAuthentication().getName().equals("anonymousUser")) {
            return memberService.findMemberById((Long) session.getAttribute("loginMemberId"));
        }
        return null;
    }
}
