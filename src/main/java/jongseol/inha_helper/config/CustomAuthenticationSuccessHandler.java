package jongseol.inha_helper.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jongseol.inha_helper.domain.Member;
import jongseol.inha_helper.service.CoursemosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final CoursemosService coursemosService;

    @Autowired
    public CustomAuthenticationSuccessHandler(CoursemosService coursemosService) {
        this.coursemosService = coursemosService;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // loginMember 를 세션에 저장
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomMemberDetails userDetails) {
            Member loginMember = userDetails.getMember();
            Long loginMemberId = loginMember.getId();

            // 세션에 Member 정보 저장
            HttpSession session = request.getSession();
            session.setAttribute("loginMemberId", loginMemberId);

            // 세션 유효 시간 설정
            session.setMaxInactiveInterval(30 * 60 + 4);

            Instant now = Instant.now();
            Instant expires = Instant.ofEpochMilli(session.getLastAccessedTime() + session.getMaxInactiveInterval() * 1000L);
            Duration remaining = Duration.between(now, expires);

            long remainingSeconds = remaining.getSeconds();
            session.setAttribute("remainingSeconds", remainingSeconds);

            response.sendRedirect("/coursemos/reload");
        }
    }
}
