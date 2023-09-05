package com.idle.fmd.domain.board.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReportDto {

    @NotBlank(message = "신고 내용은 1000자 이내로 작성해주세요")
    @Column(length = 1000)
    private String content;
}
