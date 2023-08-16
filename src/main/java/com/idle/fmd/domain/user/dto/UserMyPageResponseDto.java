package com.idle.fmd.domain.user.dto;

import com.idle.fmd.domain.lol.entity.LolEntity;
import com.idle.fmd.domain.user.entity.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMyPageResponseDto {
    private String accountId;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
    private LolEntity lolAccount;

    public static UserMyPageResponseDto fromEntity(UserEntity entity) {
        UserMyPageResponseDto dto = new UserMyPageResponseDto();
        dto.setAccountId(entity.getAccountId());
        dto.setEmail(entity.getEmail());
        dto.setNickname(entity.getNickname());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setLolAccount(entity.getLolAccount());
        return dto;
    }
}
