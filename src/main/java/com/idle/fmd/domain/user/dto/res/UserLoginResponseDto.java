package com.idle.fmd.domain.user.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponseDto {
    private String token;
    private String nickname;
    private String profileImage;

    public UserLoginResponseDto(String token) {
        this.token = token;
    }
}
