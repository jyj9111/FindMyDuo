package com.idle.fmd.domain.user.controller;

import com.idle.fmd.domain.user.dto.UserLoginRequestDto;
import com.idle.fmd.domain.user.service.UserService;
import com.idle.fmd.domain.user.dto.UserLoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.idle.fmd.domain.user.dto.SignupDto;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public void signup(@Valid @RequestBody SignupDto dto){
        userService.signup(dto);
    }

    // 로그인 유저 토큰 발급
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto dto) {
        return userService.loginUser(dto);
    }

    // OAuth 로그인 유저 토큰 발급
    @GetMapping("/oauth")
    public UserLoginResponseDto oauthLogin(@RequestParam("token") String token) {
        return new UserLoginResponseDto(token);

    }
}
