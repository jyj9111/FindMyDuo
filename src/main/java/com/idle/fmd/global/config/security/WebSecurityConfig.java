package com.idle.fmd.global.config.security;


import com.idle.fmd.global.auth.jwt.JwtTokenFilter;
import com.idle.fmd.global.auth.oauth2.OAuth2SuccessHandler;
import com.idle.fmd.global.auth.oauth2.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authHttp -> authHttp
                                .requestMatchers(
                                        HttpMethod.GET, "/board/**",
                                        "/webjars/**",
                                        "/static/**",
                                        "/main",
                                        "/login",
                                        "/signup",
                                        "/mypage"
                                )
                                .permitAll()
                                .requestMatchers(
                                        "/users/login",     // 로그인 url
                                        "/users/signup",    // 회원가입 url
                                        "/users/oauth", // oauth 로그인시 토큰발급 url
                                        "/users/email-auth", // 이메일 인증 요청 url
                                        "/users/oauth-fail" // oauth 실패 시 리다이렉트 url
                                )
                                .anonymous()
                                .requestMatchers(
                                        "/js/**",
                                        "/webjars/**",
                                        "/ws-stomp/**",
                                        "/chat/**"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/users/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler))
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtTokenFilter, AuthorizationFilter.class);

        return http.build();
    }
}
