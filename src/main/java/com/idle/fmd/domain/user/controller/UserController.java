package com.idle.fmd.domain.user.controller;

import com.idle.fmd.domain.user.dto.UserLoginRequestDto;
import com.idle.fmd.domain.user.service.UserService;
import com.idle.fmd.global.auth.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    @PostMapping("/login")
    public JwtTokenDto login(@RequestBody UserLoginRequestDto dto) {

        return userService.loginUser(dto);
    }
}
