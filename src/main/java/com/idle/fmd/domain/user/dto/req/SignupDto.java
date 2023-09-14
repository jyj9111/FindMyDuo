package com.idle.fmd.domain.user.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 회원가입 시 입력 데이터 DTO
@Data
public class SignupDto {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 6, message = "아이디는 최소 6자리입니다.")
    private String accountId;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @NotBlank(message = "패스워드를 입력해주세요.")
    @Size(min = 8, message = "패스워드는 최소 8자리입니다.")
    private String password;

    @NotBlank(message = "패스워드를 확인해주세요.")
    private String passwordCheck;

    @NotBlank(message = "이메일 인증코드를 입력해주세요.")
    private String emailAuthCode;
}
