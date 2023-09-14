package com.idle.fmd.domain.user.dto.res;

import com.idle.fmd.domain.user.entity.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMyPageResponseDto {
    private String accountId;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
    private String lolNickname;
    private String profileImage;

    public static UserMyPageResponseDto fromEntity(UserEntity entity) {
        UserMyPageResponseDto dto = new UserMyPageResponseDto();
        dto.setAccountId(entity.getAccountId());
        dto.setEmail(entity.getEmail());
        dto.setNickname(entity.getNickname());
        dto.setCreatedAt(entity.getCreatedAt());
        if(entity.getLolAccount() != null) {
            dto.setLolNickname(entity.getLolAccount().getName());
        }
        dto.setProfileImage(entity.getProfileImage());
        return dto;
    }
}
