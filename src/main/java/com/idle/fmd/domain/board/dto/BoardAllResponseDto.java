package com.idle.fmd.domain.board.dto;

import com.idle.fmd.domain.board.entity.BoardEntity;
import com.idle.fmd.domain.board.entity.BookmarkEntity;
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

    public static BoardAllResponseDto fromEntity(BookmarkEntity bookmark) {
        BoardAllResponseDto boardAllResponseDto = new BoardAllResponseDto();
        boardAllResponseDto.setId(bookmark.getBoard().getId());
        boardAllResponseDto.setNickName(bookmark.getUser().getNickname());
        boardAllResponseDto.setTitle(bookmark.getBoard().getTitle());
        boardAllResponseDto.setLiked(bookmark.getBoard().getLiked());
        boardAllResponseDto.setModifiedAt(bookmark.getBoard().getModifiedAt());

        return boardAllResponseDto;
    }
}
