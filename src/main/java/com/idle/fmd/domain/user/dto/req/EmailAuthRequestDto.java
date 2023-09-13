package com.idle.fmd.domain.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// 이메일 인증 시 요청 DTO
// 이메일 정보를 포함
@Data
public class EmailAuthRequestDto {
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;
}
