package com.idle.fmd.domain.chating.controller;

import com.idle.fmd.domain.chating.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessageSendingOperations messagingTemplate;

    // 채팅방 입장 화면
    @GetMapping("/chat/room/enter")
    public String roomDetail() {
        return "matching/chat";
    }

    // 채팅(STOMP)방 입장, 대화
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            log.info("{} 채팅방(id:{}) 입장", message.getSender(), message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        messagingTemplate.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);
    }
}
