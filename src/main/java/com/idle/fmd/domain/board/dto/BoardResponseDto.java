package com.idle.fmd.domain.board.dto;

import com.idle.fmd.domain.board.entity.BoardEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardResponseDto {

    private Long id;

    private String nickName;

    private String title;

    private String content;

    private LocalDateTime modifiedAt;

    public static BoardResponseDto fromEntity(BoardEntity board) {
        BoardResponseDto boardResponseDto = new BoardResponseDto();
        boardResponseDto.setId(board.getId());
        boardResponseDto.setNickName(board.getUser().getNickname());
        boardResponseDto.setTitle(board.getTitle());
        boardResponseDto.setContent(board.getContent());
        boardResponseDto.setModifiedAt(board.getModifiedAt());

        return boardResponseDto;
    }
}
