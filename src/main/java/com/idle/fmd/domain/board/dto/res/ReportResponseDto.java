package com.idle.fmd.domain.board.dto.res;

import com.idle.fmd.domain.board.entity.BoardEntity;
import lombok.Data;

@Data
public class ReportResponseDto {

    private Integer reported;
    private String message;

    public static ReportResponseDto fromEntity(BoardEntity board, String message) {
        ReportResponseDto dto = new ReportResponseDto();

        if (board == null) {
            dto.setReported(2);
            dto.setMessage(message);
        } else {
            dto.setReported(board.getReported());
            dto.setMessage(message);
        }

        return dto;
    }
}
