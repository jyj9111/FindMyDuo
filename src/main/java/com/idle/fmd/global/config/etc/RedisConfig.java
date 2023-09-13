package com.idle.fmd.global.config.etc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Redis 접속 정보와 Redis 템플릿 설정 클래스
@Configuration
public class RedisConfig {
    @Value("${spring.cache.redis.host}")
    private String host;
    @Value("${spring.cache.redis.port}")
    private int port;

    // Redis 연결 정보 설정
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Redis 설정 객체 생성
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        // Hostname 설정
        redisConfiguration.setHostName(host);
        // 포트번호 설정
        redisConfiguration.setPort(port);

        // Redis 연결 객체생성 후 위의 설정 삽입
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration);

        return lettuceConnectionFactory;
    }

    // Redis 와 통신하기 위한 RedisTemplate 객체를 빈 객체로 등록
    @Primary
    @Bean
    public RedisTemplate<String, String> redisTemplate(){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        // Redis 연결 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // 데이터 직렬화 역직렬화 기능 추가
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return  redisTemplate;
    }
}
