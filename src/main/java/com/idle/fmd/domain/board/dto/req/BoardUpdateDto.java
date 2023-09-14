package com.idle.fmd.domain.board.dto.req;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BoardUpdateDto {

    @NotEmpty(message = "자유게시판 제목은 100자 이내로 작성하셔야 합니다.")
    @Column(length = 100)
    private String title;

    @NotEmpty(message = "자유게시판 내용은 1000자 이내로 작성하셔야 합니다.")
    @Column(length = 1000)
    private String content;
}
