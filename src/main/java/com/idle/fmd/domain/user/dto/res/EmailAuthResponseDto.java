package com.idle.fmd.domain.user.dto.res;

import lombok.Data;

// 이메일 인증 성공 시 응답 DTO
// 이메일 인증코드 정보를 포함
@Data
public class EmailAuthResponseDto {
    private int authCode;

    public EmailAuthResponseDto(int authCode){
        this.authCode = authCode;
    }
}
