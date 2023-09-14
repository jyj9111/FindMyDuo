package com.idle.fmd.global.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.fmd.domain.user.dto.res.UserLoginResponseDto;
import com.idle.fmd.domain.user.entity.CustomUserDetails;
import com.idle.fmd.global.utils.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisUtil redisUtil;
    private final WebClient webClient;
    // ObjectMapper 는 스프링의 기본 Bean 객체로 따로 등록해주지 않아도 DI 를 통해 사용 가능
    private final ObjectMapper objectMapper;

    public JwtTokenFilter(JwtTokenUtils jwtTokenUtils, RedisUtil redisUtil, WebClient webClient, ObjectMapper objectMapper) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.redisUtil = redisUtil;
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String requestUrl = request.getRequestURI();

        if (authHeader != null && authHeader.startsWith("Bearer ") || requestUrl.startsWith("/ws/matching")) {
            String token;

            if(requestUrl.startsWith("/ws/matching")) {
                token = request.getQueryString().replace("Authorization=", "");
                token = token.substring(0,token.indexOf("&"));
            }
            else token = authHeader.split(" ")[1];

            // 토큰이 유효하다면 인증정보 등록 후 다음 필터 실행
            if(jwtTokenUtils.validate(token)){
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                String username = jwtTokenUtils.parseClaims(token).getSubject();

                AbstractAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(
                        CustomUserDetails.builder()
                                .accountId(username)
                                .build(), token, new ArrayList<>());

                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);
                log.debug("set security context with jwt");
            }
            else{
                // 액세스 토큰이 만료됐을 경우 리프레쉬 토큰이 존재할 때 새로운 액세스 토큰을 발급
                if(redisUtil.hasRefreshToken(token)){
                    log.warn("invalid token: " + token);
                    // redisUtil 의 issueNewAccessToken() 메서드로 새로운 토큰을 생성
                    String newAccessToken = redisUtil.issueNewAccessToken(token);

                    // 생성한 새로운 토큰을 reissueToken() 메서드를 통해 DTO 형태로 받아옴
                    UserLoginResponseDto userLoginResponseDto = reissueToken(newAccessToken);
                    // 새로운 토큰 데이터를 아래의 response.getWriter().write() 에서 사용하기 위해 DTO 객체를 문자열 형태로 변환
                    String responseBody = objectMapper.writeValueAsString(userLoginResponseDto);

                    // 응답 바디의 데이터 형태를 JSON 으로 설정 후 데이터와 함께 응답
                    response.setContentType("application/json");
                    response.getWriter().write(responseBody);

                    log.info("새 AccessToken 발급: " + newAccessToken);
                }
                // 리프레쉬 토큰을 가지고 있지 않으면 로그인되어 있지 않은 것으로 간주한다.
                else{
                    log.error("Does not have refresh token ( 로그아웃 상태 )");
                    response.setStatus(403);
                    log.info(token);
                }
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // 웹 클라이언트를 사용해서 URL 을 호출하여 응답 결과를 UserLoginResponseDto 형태로 가져온다.
    // 리다이렉트 시 Authorization 헤더가 전달되지 않는 점을 해결하기 위해 사용
    public UserLoginResponseDto reissueToken(String newAccessToken){
        String url = "/users/reissue-token";

        UserLoginResponseDto result = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .queryParam("token", newAccessToken)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken )
                .retrieve()
                .bodyToMono(UserLoginResponseDto.class)
                .block();

        return result;
    }
}