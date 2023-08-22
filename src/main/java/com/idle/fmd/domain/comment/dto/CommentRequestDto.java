package com.idle.fmd.domain.comment.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CommentRequestDto {
    @NotEmpty(message = "댓글은 100자 이내로 작성하셔야 합니다.")
    @Column(length = 100)
    private String content;
}
