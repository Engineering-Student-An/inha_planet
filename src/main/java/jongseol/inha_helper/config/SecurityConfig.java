package jongseol.inha_helper.config;

import jongseol.inha_helper.service.CoursemosService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/myPage/**").authenticated()
                        .anyRequest().permitAll()
                );

        http
                .formLogin((auth) -> auth.loginPage("/login")

                        .loginProcessingUrl("/login")
//                        .defaultSuccessUrl("/", true)
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .permitAll()
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(new CustomAuthenticationFailureHandler())
                );

        http
                .logout((auth) -> auth
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true));


        http
                .csrf((auth) -> auth.disable());

//        http
//                .csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                        .ignoringRequestMatchers("/api/**")
//                );

        return http.build();
    }

    // bcrpytpasswordEncoder
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){

        return new BCryptPasswordEncoder();
    }
}
