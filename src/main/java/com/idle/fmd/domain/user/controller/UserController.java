package com.idle.fmd.domain.user.controller;

import com.idle.fmd.domain.user.dto.SignupDto;
import com.idle.fmd.domain.user.service.UserService;
import jakarta.validation.Valid;
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

    @PostMapping("/signup")
    public void signup(@Valid @RequestBody SignupDto dto){
        userService.signup(dto);
    }
}
