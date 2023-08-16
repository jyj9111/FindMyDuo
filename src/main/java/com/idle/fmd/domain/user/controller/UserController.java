package com.idle.fmd.domain.user.controller;

import com.idle.fmd.domain.user.dto.*;
import com.idle.fmd.domain.user.service.UserService;
import com.idle.fmd.global.error.exception.BusinessException;
import com.idle.fmd.global.error.exception.BusinessExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    // 인증 메일 발송
    @PostMapping("/email-auth")
    public void sendEmail(@RequestBody EmailAuthRequestDto dto){
        // 인증 코드를 받아서 저장
        userService.sendEmail(dto);
    }

    // OAuth 인증 실패 시 리다이렉트 할 URL 요청
    @GetMapping("/oauth-fail")
    public void oauthFail(){
        throw new BusinessException(BusinessExceptionCode.UNAVAILABLE_OAUTH_ACCOUNT_ERROR);
    }

    // 로그아웃
    @PostMapping("/logout")
    public void logout(HttpServletRequest request){
        // 요청의 헤더정보를 가져와 토큰 내용을 추출
        String token = request.getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1];
        // 토큰을 전달
        userService.logout(token);
    }

    // 마이페이지 유저 정보 조회
    @GetMapping("/mypage")
    public UserMyPageResponseDto myPage(Authentication authentication) {
        String accountId = authentication.getName();
        UserMyPageResponseDto user = userService.profile(accountId);
        return user;
    }

    // 마이페이지 유저 정보 수정
    @PutMapping("/mypage")
    public UserMyPageRequestDto updateMyPage(
            Authentication authentication,
            @RequestBody UserMyPageRequestDto dto) {
        String accountId = authentication.getName();
        return userService.update(accountId, dto);
    }

    // 마이페이지 회원 탈퇴 (유저 정보 삭제)
    @DeleteMapping("/mypage")
    public void UserDelete(Authentication authentication) {
        String accountId = authentication.getName();
        userService.delete(accountId);
    }
}
