package com.idle.fmd.domain.user.dto;

import lombok.Data;


@Data
public class UserLoginResponseDto {
    private String token;
    
    public UserLoginResponseDto(String token) {
        this.token = token;
    }
}
