package com.idle.fmd.domain.user.service;

import com.idle.fmd.domain.user.dto.SignupDto;
import com.idle.fmd.domain.user.entity.CustomUserDetails;
import com.idle.fmd.global.error.exception.BusinessException;
import com.idle.fmd.global.error.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final CustomUserDetailsManager manager;

    // 회원가입 메서드
    public void signup(SignupDto dto){
        String password = dto.getPassword();
        String passwordCheck = dto.getPasswordCheck();

        // 비밀번호와 비밀번호 확인 데이터가 다르면 예외 발생
        if(!password.equals(passwordCheck))
            throw new BusinessException(BusinessExceptionCode.PASSWORD_CHECK_ERROR);

        // CustomUserDetailsManager 의 createUser 메서드를 호출해서 유저를 등록 ( UserDetails 객체 전달 필요 )
        manager.createUser(
                CustomUserDetails.builder()
                .accountId(dto.getAccountId())
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .build()
        );
    }
}
