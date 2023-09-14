package com.idle.fmd.domain.matching.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.fmd.domain.chating.dto.ChatRoom;
import com.idle.fmd.domain.chating.sevice.ChatRoomService;
import com.idle.fmd.domain.matching.dto.MatchingSuccessDto;
import com.idle.fmd.global.exception.BusinessException;
import com.idle.fmd.global.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {
    private final ObjectMapper objectMapper;
    private final ChatRoomService chatRoomService;
    private final DiscordService discordService;

    public void openChatRoom(WebSocketSession session, WebSocketSession destination) {
        // 채팅방 이름 및 디스코드 음성채널 이름 설정
        String commonName = String.format(
                "%s-%s",
                session.getAttributes().get("nickname"),
                destination.getAttributes().get("nickname"));
        // 채팅방 객체 생성
        ChatRoom chatRoom = chatRoomService.createChatRoom(commonName);
        // 채팅방 입장 엔드포인트 Url
        String url = "/chat/room/enter";
        // Discord 음성채널 생성
        Optional<String> optionalUrl = discordService.createChannel(commonName, 2);
        if (optionalUrl.isEmpty())
            throw new BusinessException(BusinessExceptionCode.CANNOT_CREATE_VOICE_CHANNEL);
        MatchingSuccessDto message =
                new MatchingSuccessDto(chatRoom.getRoomId(), commonName, url, optionalUrl.get());

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
