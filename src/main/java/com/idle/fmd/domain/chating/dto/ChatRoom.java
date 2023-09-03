package com.idle.fmd.domain.chating.dto;


import lombok.Data;

import java.util.UUID;

@Data
public class ChatRoom { // 채팅방 클래스
    private String roomId;
    private String name;

    public static ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.name = name;
        return chatRoom;
    }
}
