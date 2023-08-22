package com.idle.fmd.domain.comment.dto;

import com.idle.fmd.domain.comment.entity.CommentEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {

    private Long id;

    private Long boardId;

    private String nickname;

    private String content;

    private LocalDateTime modifiedAt;

    public static CommentResponseDto fromEntity(CommentEntity entity){ //엔티티를 dto 로 변경해줌
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(entity.getId());
        commentResponseDto.setBoardId(entity.getBoardId());
        commentResponseDto.setNickname(entity.getUser().getNickname());
        commentResponseDto.setContent(entity.getContent());
        commentResponseDto.setModifiedAt(entity.getModifiedAt());
        return commentResponseDto;
    }
}
