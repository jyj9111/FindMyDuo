package com.idle.fmd.domain.chating.controller;

import com.idle.fmd.domain.chating.dto.ChatRoom;
import com.idle.fmd.domain.chating.sevice.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomService.findRoomById(roomId);
    }
}
