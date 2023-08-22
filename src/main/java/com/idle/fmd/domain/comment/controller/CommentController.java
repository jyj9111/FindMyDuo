package com.idle.fmd.domain.comment.controller;


import com.idle.fmd.domain.comment.dto.CommentRequestDto;
import com.idle.fmd.domain.comment.dto.CommentResponseDto;
import com.idle.fmd.domain.comment.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/board/{boardId}/comment")
public class CommentController {
    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }


    @PostMapping
    public CommentResponseDto createComment(
            @PathVariable ("boardId") Long boardId,
            Authentication authentication,
            @RequestBody CommentRequestDto dto
    ) {
       return service.createComment(boardId, authentication, dto);
    }


    @GetMapping
    public List<CommentResponseDto> readAllComment(
            @PathVariable("boardId") Long boardId
    ){
        return service.readCommentAll(boardId);
    }


    @PutMapping("/{commentId}")
    public CommentResponseDto updateComment(
            @PathVariable("boardId") Long boardId,
            @PathVariable("commentId") Long commentId,
            Authentication authentication,
            @RequestBody CommentRequestDto dto
    ){
        return service.updateComment(boardId, commentId, authentication, dto);
    }


    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable("boardId") Long boardId,
            @PathVariable("commentId") Long commentId,
            Authentication authentication,
            LocalDateTime localDateTime
    ){
        LocalDateTime time = localDateTime.now();
        service.deleteComment(boardId, commentId, authentication, time);
    }






}
