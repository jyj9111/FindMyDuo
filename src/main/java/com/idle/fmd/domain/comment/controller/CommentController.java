package com.idle.fmd.domain.comment.controller;


import com.idle.fmd.domain.comment.dto.CommentRequestDto;
import com.idle.fmd.domain.comment.dto.CommentResponseDto;
import com.idle.fmd.domain.comment.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/board/{board}/comment")
public class CommentController {
    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }


    @PostMapping
    public CommentResponseDto createComment(
            @PathVariable ("board") Long board,
            Authentication authentication,
            @Validated @RequestBody CommentRequestDto dto
    ) {
       return service.createComment(board, authentication, dto);
    }


    @GetMapping
    public List<CommentResponseDto> readAllComment(
            @PathVariable("board") Long board
    ){
        return service.readCommentAll(board);
    }


    @PutMapping("/{commentId}")
    public CommentResponseDto updateComment(
            @PathVariable("board") Long board,
            @PathVariable("commentId") Long commentId,
            Authentication authentication,
            @Validated @RequestBody CommentRequestDto dto
    ){
        return service.updateComment(board, commentId, authentication, dto);
    }


    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable("board") Long board,
            @PathVariable("commentId") Long commentId,
            Authentication authentication
    ){
        service.deleteComment(board, commentId, authentication);
    }






}
