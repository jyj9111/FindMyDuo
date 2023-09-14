package com.idle.fmd.domain.user.dto.req;

import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    private String password;
    private String passwordCheck;
}
