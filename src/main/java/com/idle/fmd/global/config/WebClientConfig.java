package com.idle.fmd.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

// 웹 클라이언트 객체 설정파일
// 웹 클라이언트의 기본 설정을 하고 빈 으로 등록하기 위해 사용
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(){
        // 프로젝트를 클라우드 서버를 통해 배포시 반드시 baseUrl 수정 필요!!
        return WebClient.builder().baseUrl("http://localhost:8080").defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    }
}
