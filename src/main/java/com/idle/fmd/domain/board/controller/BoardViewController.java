package com.idle.fmd.domain.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/board")
@RequiredArgsConstructor
@Controller
public class BoardViewController {

    // 단일조회
    @GetMapping("/view/{boardId}")
    public String read() {
        return "/board/board";
    }

    // 글 수정
    @GetMapping("/form/{boardId}")
    public String update() {
        return "/board/updateboard";
    }
}
