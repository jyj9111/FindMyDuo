package com.idle.fmd.domain.matching;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.fmd.domain.chating.dto.ChatRoom;
import com.idle.fmd.domain.chating.sevice.ChatRoomService;
import com.idle.fmd.domain.matching.MatchingSuccessDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {
    private final ObjectMapper objectMapper;
    private final ChatRoomService chatRoomService;

    public void openChatRoom(String roomName, WebSocketSession session, WebSocketSession destination) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(roomName);
        String url = "/chat/room/enter";
        MatchingSuccessDto message = new MatchingSuccessDto(chatRoom.getRoomId(), roomName, url);

        this.sendMessage(session, message);
        this.sendMessage(destination, message);
    }

    // 메시지 발송
    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
