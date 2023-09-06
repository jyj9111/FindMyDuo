package com.idle.fmd.domain.chating.dto;

import lombok.Data;

@Data
public class ChatMessage {
    public enum MessageType {
        ENTER,  // 입장
        TALK    // 채팅
    }

    private MessageType type;   // 메세지 타입
    private String roomId;      // 방 고유번호
    private String sender;      // 보낸사람
    private String message;     // 내용
}
