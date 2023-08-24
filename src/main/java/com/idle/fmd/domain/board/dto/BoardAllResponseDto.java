package com.idle.fmd.domain.board.dto;

import com.idle.fmd.domain.board.entity.BoardEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardAllResponseDto {

    private Long id;

    private String nickName;

    private String title;

    private Integer liked;

    private LocalDateTime modifiedAt;

    public static BoardAllResponseDto fromEntity(BoardEntity board) {
        BoardAllResponseDto boardAllResponseDto = new BoardAllResponseDto();
        boardAllResponseDto.setId(board.getId());
        boardAllResponseDto.setNickName(board.getUser().getNickname());
        boardAllResponseDto.setTitle(board.getTitle());
        boardAllResponseDto.setLiked(board.getLiked());
        boardAllResponseDto.setModifiedAt(board.getModifiedAt());

        return boardAllResponseDto;
    }
}
