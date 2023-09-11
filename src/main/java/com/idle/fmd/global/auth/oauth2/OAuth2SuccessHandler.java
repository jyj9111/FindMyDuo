package com.idle.fmd.global.auth.oauth2;

import com.idle.fmd.domain.user.entity.CustomUserDetails;
import com.idle.fmd.domain.user.service.CustomUserDetailsManager;
import com.idle.fmd.global.auth.jwt.JwtTokenUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenUtils tokenUtils;
    private final CustomUserDetailsManager manager;

    @Override
    // 인증 성공시 호출되는 메소드
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User
                = (OAuth2User) authentication.getPrincipal();

        String provider = oAuth2User.getAttribute("provider");
        String providerId = oAuth2User.getAttribute("id").toString();
        String accountId = String.format("%s_%s", provider, providerId);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name").toString();
        String nickname = name + "_" + providerId;


        // 처음으로 소셜 로그인한 사용자를 데이터베이스에 등록
        if(!manager.userExists(accountId) && !manager.existByEmail(email)) {
            manager.createUser(CustomUserDetails.builder()
                    .accountId(accountId)
                    .email(email)
                    .nickname(nickname)
                    .password(providerId)
                    .build());
        }
        // 도메인 주소값 추출
        String domainUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        try{
            // 데이터베이스에서 사용자 회수
            UserDetails details
                    = manager.loadUserByUsername(accountId);

            String jwt = tokenUtils.generateToken(details);

            // 목적지 URL 설정
            String targetUrl = String.format("%s/main?token=%s", domainUrl, jwt);
            // 실제 Redirect 응답 생성
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        }catch (Exception e){
            log.error(e.getMessage());
            // oauth 인증 실패 시 oauth-fail URL 로 리다이렉트
            String targetUrl = String.format("%s/login?oauth-fail", domainUrl);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}
