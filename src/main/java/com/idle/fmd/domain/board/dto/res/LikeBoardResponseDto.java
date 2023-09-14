package com.idle.fmd.domain.board.dto.res;

import com.idle.fmd.domain.board.entity.BoardEntity;
import lombok.Data;

@Data
public class LikeBoardResponseDto {

    private Integer liked;
    private String message;

    public static LikeBoardResponseDto fromEntity(BoardEntity board, String message) {
        LikeBoardResponseDto dto = new LikeBoardResponseDto();
        dto.setLiked(board.getLiked());
        dto.setMessage(message);

        return dto;
    }
}
