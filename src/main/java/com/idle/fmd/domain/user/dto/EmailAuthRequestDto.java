package com.idle.fmd.domain.user.dto;

import lombok.Data;

// 이메일 인증 시 요청 DTO
// 이메일 정보를 포함
@Data
public class EmailAuthRequestDto {
    private String email;
}
