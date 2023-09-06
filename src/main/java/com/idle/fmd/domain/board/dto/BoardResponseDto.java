package com.idle.fmd.domain.board.dto;

import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.board.entity.FileEntity;
import com.idle.fmd.domain.comment.dto.CommentResponseDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BoardResponseDto {
    private Long id;

    private String nickName;

    private String accountId;

    private String title;

    private String content;

    private Integer liked;

    private List<String> images;

    private List<CommentResponseDto> comments;

    private LocalDateTime modifiedAt;

    private Integer view;

    public static BoardResponseDto fromEntity(BoardEntity board) {
        BoardResponseDto boardResponseDto = new BoardResponseDto();
        boardResponseDto.setId(board.getId());
        boardResponseDto.setNickName(board.getUser().getNickname());
        boardResponseDto.setAccountId(board.getUser().getAccountId());
        boardResponseDto.setTitle(board.getTitle());
        boardResponseDto.setLiked(board.getLiked());
        boardResponseDto.setContent(board.getContent());
        boardResponseDto.setView(board.getView());

        List<String> images = new ArrayList<>();
        for (FileEntity entity : board.getFiles()) {
            images.add(entity.getImageUrl());
        }
        boardResponseDto.setImages(images);
        boardResponseDto.setComments(board.getComments().stream().map(CommentResponseDto::fromEntity).toList());
        boardResponseDto.setModifiedAt(board.getModifiedAt());

        return boardResponseDto;
    }
}
