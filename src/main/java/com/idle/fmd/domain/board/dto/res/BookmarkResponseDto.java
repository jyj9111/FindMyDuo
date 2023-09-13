package com.idle.fmd.domain.board.dto.res;

import com.idle.fmd.domain.board.entity.BoardEntity;
import lombok.Data;

@Data
public class BookmarkResponseDto {
    private Integer bookmarked;
    private String message;

    public static BookmarkResponseDto fromEntity(BoardEntity board, String message) {
        BookmarkResponseDto dto = new BookmarkResponseDto();
        dto.setBookmarked(board.getBookmarked());
        dto.setMessage(message);

        return dto;
    }
}
