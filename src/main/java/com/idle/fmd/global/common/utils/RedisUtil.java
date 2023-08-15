package com.idle.fmd.global.common.utils;

import com.idle.fmd.global.auth.jwt.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

// Redis 관련 기능을 정의하는 클래스
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenUtils jwtTokenUtils;

    // 로그아웃 시 해당 토큰을 블랙리스트로 저장하는 메서드
    public void setBlackListToken(String token){
        // 토큰을 받아와서 토큰을 해석
        Claims tokenInfo = jwtTokenUtils.parseClaims(token);

        // 토큰의 유효시간을 알아낸다.
        long tokenExpirationPeriod = (tokenInfo.getExpiration().getTime() / 1000 - tokenInfo.getIssuedAt().getTime() / 1000);
        log.info("토큰 유효시간: " + tokenExpirationPeriod);

        // 토큰의 남은 유효시간을 알아낸다.
        long expiration = tokenExpirationPeriod - (Instant.now().getEpochSecond() - tokenInfo.getIssuedAt().getTime() / 1000);
        log.info("남은 유효시간:" + expiration);

        // blackListToken:토큰 형태로 키를 등록한다.
        String key = "blackListToken:" + token;

        // 토큰의 남은 시간동안 해당 토큰을 블랙리스트로 처리하고 value 는 "logout" 으로 저장한다.
        redisTemplate.opsForValue().set(key, "logout", Duration.ofSeconds(expiration));
    }

    // Redis DB 에 ["authCode:이메일" : "인증코드"] 형태로 데이터를 저장하고 데이터 만료시간은 300초로 한다.
    // 만약 해당 이메일에 대한 데이터가 있다면 인증코드와 만료시간을 갱신한다.
    public void setEmailAuthCode(String email, int authCode){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String key = "authCode:" + email;
        values.set(key, String.valueOf(authCode), Duration.ofSeconds(300));
    }

    public void delete(String email){
        String key = "authCode:" + email;
        redisTemplate.delete(key);
    }

    public Object getAuthCode(String email){
        String key = "authCode:" + email;
        return redisTemplate.opsForValue().get(key);
    }
}
