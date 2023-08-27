package com.idle.fmd.domain.user.dto;

import lombok.Data;


@Data
public class UserLoginResponseDto {
    private String token;

    // JwtFilter 의 웹 클라이언트 사용 시 기본 생성자가 필요해서 기본 생성자 추가
    public UserLoginResponseDto(){}
    public UserLoginResponseDto(String token) {
        this.token = token;
    }
}
