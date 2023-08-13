package com.idle.fmd.domain.user.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

import java.util.Collection;

@Builder
public class CustomUserDetails implements UserDetails {
    private String accountId;
    private String password;
    @Getter
    private String email;
    @Getter
    private String nickname;

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.accountId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public static CustomUserDetails fromEntity(UserEntity entity) {

        return CustomUserDetails.builder()
                .accountId(entity.getAccountId())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .build();
    }

    public UserEntity newEntity() {
        UserEntity entity = new UserEntity();
        entity.setAccountId(accountId);
        entity.setPassword(password);
        entity.setEmail(email);
        entity.setNickname(nickname);
        entity.setCreatedAt(LocalDateTime.now());

        return entity;
    }

}
