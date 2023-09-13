package com.idle.fmd.global.config.websocket;

import com.idle.fmd.domain.matching.service.MatchingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

// 웹 소켓 설정 클래스

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final MatchingHandler matchingHandler;

    // 웹 소켓 핸들러를 등록하는 메서드
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // ws/matching URL 이 요청이 들어오면 matchingHandler 가 실행되도록 연결
        // setAllowedOrigins 로 이 URL 이 실행될 수 있는 출처 ( 주소 ) 를 설정
        registry.addHandler(matchingHandler, "ws/matching")
                .setAllowedOrigins("*");
    }
}
