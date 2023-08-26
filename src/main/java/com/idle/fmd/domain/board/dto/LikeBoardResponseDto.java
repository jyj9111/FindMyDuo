package com.idle.fmd.domain.board.dto;

import com.idle.fmd.domain.board.entity.BoardEntity;
import lombok.Data;

@Data
public class LikeBoardResponseDto {

    private Integer liked;

    public static LikeBoardResponseDto fromEntity(BoardEntity board) {
        LikeBoardResponseDto dto = new LikeBoardResponseDto();
        dto.setLiked(board.getLiked());

        return dto;
    }
}
