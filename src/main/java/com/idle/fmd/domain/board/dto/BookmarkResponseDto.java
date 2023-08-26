package com.idle.fmd.domain.board.dto;

import com.idle.fmd.domain.board.entity.BoardEntity;
import lombok.Data;

@Data
public class BookmarkResponseDto {
    private Integer bookmarked;

    public static BookmarkResponseDto fromEntity(BoardEntity board) {
        BookmarkResponseDto dto = new BookmarkResponseDto();
        dto.setBookmarked(board.getBookmarked());

        return dto;
    }
}
