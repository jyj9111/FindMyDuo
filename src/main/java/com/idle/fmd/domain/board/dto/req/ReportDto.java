package com.idle.fmd.domain.board.dto.req;

import jakarta.persistence.Column;
import lombok.Getter;

@Getter
public class ReportDto {

    @Column(length = 1000)
    private String content;
}
