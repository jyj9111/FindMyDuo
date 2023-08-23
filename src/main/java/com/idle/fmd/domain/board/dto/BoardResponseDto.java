package com.idle.fmd.domain.board.dto;

import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.file.entity.FileEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BoardResponseDto {

    private Long id;

    private String nickName;

    private String title;

    private String content;

    private List<String> images;

    private LocalDateTime modifiedAt;

    public static BoardResponseDto fromEntity(BoardEntity board) {
        BoardResponseDto boardResponseDto = new BoardResponseDto();
        boardResponseDto.setId(board.getId());
        boardResponseDto.setNickName(board.getUser().getNickname());
        boardResponseDto.setTitle(board.getTitle());
        boardResponseDto.setContent(board.getContent());

        List<String> images = new ArrayList<>();
        for (FileEntity entity : board.getFiles()) {
            images.add(entity.getImageUrl());
        }
        boardResponseDto.setImages(images);

        boardResponseDto.setModifiedAt(board.getModifiedAt());

        return boardResponseDto;
    }
}
