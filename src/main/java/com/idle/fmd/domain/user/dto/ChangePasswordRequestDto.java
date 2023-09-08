package com.idle.fmd.domain.user.dto;

import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    private String password;
    private String passwordCheck;
}
