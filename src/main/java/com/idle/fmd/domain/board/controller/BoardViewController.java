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
        return "board/board";
    }

    // 글 작성 폼으로 이동
    @GetMapping("/form/write")
    public String writeForm() {
        return "board/board-write-form";
    }

    // 글 수정 폼으로 이동
    @GetMapping("/form/{boardId}")
    public String updateForm() {
        return "board/board-update-form";
    }

    // 게시글 전체 조회
    @GetMapping("/view")
    public String board(){
        return "board/board-list";
    }

    // 북마크 게시물 조회
    @GetMapping("/bookmark")
    public String bookmark(){
        return "board/board-bookmark";
    }
}
