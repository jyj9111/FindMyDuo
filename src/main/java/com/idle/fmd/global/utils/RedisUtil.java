package com.idle.fmd.global.utils;

import com.idle.fmd.domain.user.entity.CustomUserDetails;
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

    // 이메일 인증이 완료되면 Redis 에 저장된 인증코드를 삭제하는 메서드
    public void delete(String email){
        String key = "authCode:" + email;
        redisTemplate.delete(key);
    }

    // 해당하는 이메일로 보내진 인증코드를 반환하는 메서드
    public Object getAuthCode(String email){
        String key = "authCode:" + email;
        return redisTemplate.opsForValue().get(key);
    }

    // 리프레쉬 토큰을 발행해서 Redis 에 저장하는 메서드
    // 리프레쉬 토큰의 유효기간은 6시간으로 설정
    // ["refreshToken:액세스토큰" : "유저 아이디"] 형태로 저장
    public void issueRefreshToken(String token){
        String key = "refreshToken:" + token;
        String accountId = jwtTokenUtils.parseClaims(token).getSubject();
        redisTemplate.opsForValue().set(key, accountId, Duration.ofSeconds(3600 * 6));
    }

    // 해당 액세스 토큰에 대한 리프레쉬 토큰이 존재하는 지 확인하는 메서드
    public boolean hasRefreshToken(String token){
        String key = "refreshToken:" + token;
        return redisTemplate.hasKey(key);
    }

    // 새로운 액세스 토큰을 만드는 메서드
    public String issueNewAccessToken(String token){
        // 기존 리프레쉬 토큰에 접근해서 유저 아이디를 알아낸다.
        String key = "refreshToken:" + token;
        String accountId = redisTemplate.opsForValue().get(key);

        // 알아낸 유저 아이디로 새로운 액세스 토큰을 만든다.
        String newAccessToken = jwtTokenUtils.generateToken(CustomUserDetails.builder().accountId(accountId).build());

        // 리프레쉬 토큰의 키 값을 "refreshToken:새로운 액세스토큰" 형태로 바꿔서 저장한다.
        String newKey = "refreshToken:" + newAccessToken;
        redisTemplate.rename(key, newKey);

        // 만약 리프레쉬 토큰의 유효기간이 30분 ( 1800 초 ) 이내이면 리프레쉬 토큰의 유효기간을 6시간으로 다시 갱신해준다.
        if(redisTemplate.getExpire(newKey) <= 1800)
            redisTemplate.expire(newKey, Duration.ofSeconds(3600 * 6));

        return newAccessToken;
    }

    // 리프레쉬 토큰을 삭제하는 메서드 ( 로그아웃 시 사용 )
    public void removeRefreshToken(String token){
        String key = "refreshToken:" + token;
        redisTemplate.delete(key);
    }
}
