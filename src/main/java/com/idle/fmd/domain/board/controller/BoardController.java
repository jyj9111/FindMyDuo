package com.idle.fmd.domain.board.controller;

import com.idle.fmd.domain.board.dto.BoardCreateDto;
import com.idle.fmd.domain.board.dto.BoardResponseDto;
import com.idle.fmd.domain.board.dto.BoardUpdateDto;
import com.idle.fmd.domain.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/board")
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    // 게시글 작성
    @PostMapping
    public BoardResponseDto boardCreate(@Valid @RequestBody BoardCreateDto dto, Authentication authentication) {
        return boardService.boardCreate(dto, authentication.getName());
    }

    // 게시글 단일조회
    @GetMapping("/{boardId}")
    public BoardResponseDto boardRead(@PathVariable Long boardId) {
        return boardService.boardRead(boardId);
    }

    // 게시글 수정
    @PutMapping("/{boardId}")
    public BoardResponseDto boardUpdate(@Valid @RequestBody BoardUpdateDto dto, Authentication authentication, @PathVariable Long boardId) {
       return boardService.boardUpdate(dto, authentication.getName(), boardId);
    }

    // 게시글 soft delete
    @DeleteMapping("/{boardId}")
    public void boardDelete(Authentication authentication, @PathVariable Long boardId) {
        boardService.boardDelete(authentication.getName(), boardId);
    }

    // 전체조회 (페이징 처리)
    @GetMapping()
    public Page<BoardResponseDto> boardReadAll(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return boardService.boardReadAll(pageable);
    }
}
