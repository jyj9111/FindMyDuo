package com.idle.fmd.global.config.etc;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

// 웹 클라이언트 객체 설정파일
// 웹 클라이언트의 기본 설정을 하고 빈 으로 등록하기 위해 사용
@Slf4j
@Configuration
public class WebClientConfig {
    @Value("${server.host}")
    private String host;
    @Value("${server.port}")
    private String port;
    @Bean
    public WebClient webClient(){
        String url = "http://" + host + ":" + port;
        return WebClient.builder().baseUrl(url).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    }
}
