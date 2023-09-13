package com.idle.fmd.domain.user.controller;

import com.idle.fmd.domain.board.dto.res.BoardAllResponseDto;
import com.idle.fmd.domain.user.dto.req.*;
import com.idle.fmd.domain.user.dto.res.UserLoginResponseDto;
import com.idle.fmd.domain.user.dto.res.UserMyPageResponseDto;
import com.idle.fmd.domain.user.service.UserService;
import com.idle.fmd.global.exception.BusinessException;
import com.idle.fmd.global.exception.BusinessExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        return userService.loginForOauthUser(token);
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

    // 액세스 토큰 만료 시 토큰을 재발급 받기 위한 URL 요청
    @GetMapping("/reissue-token")
    public UserLoginResponseDto issueNewToken(@RequestParam("token") String token) {
        return new UserLoginResponseDto(token);
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

    // 비밀번호 변경
    @PutMapping("/mypage/change-password")
    public String changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequestDto dto) {
        userService.changePassword(authentication.getName(), dto);
        return "변경이 완료되었습니다.";
    }

    // 마이페이지 회원 탈퇴 (유저 정보 삭제)
    @DeleteMapping("/mypage")
    public void UserDelete(Authentication authentication) {
        String accountId = authentication.getName();
        userService.delete(accountId);
    }

    // 마이페이지 프로필 이미지 등록 및 변경
    @PutMapping(value = "/mypage/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadProfileImage(
            Authentication authentication,
            @RequestParam("image") MultipartFile image) {
        String accountId = authentication.getName();
        return userService.uploadProfileImage(accountId, image);
    }

    // 즐겨찾기 한 글 조회
    @GetMapping("/bookmark")
    public Page<BoardAllResponseDto> findBookmark(Authentication authentication, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return userService.findBookmark(authentication.getName(), pageable);
    }

    // 아이디 중복 확인
    @GetMapping("/check/accountId")
    public boolean existsByAccountId(@RequestParam("accountId") String accountId) {
        return userService.existsByAccountId(accountId);
    }

    // 닉네임 중복 확인
    @GetMapping("/check/nickname")
    public boolean existsByNickname(@RequestParam("nickname") String nickname) {
        return userService.existsByNickname(nickname);
    }

    // 이메일 중복 확인
    @GetMapping("/check/email")
    public boolean existsByEmail(@RequestParam("email") String email) {
        return userService.existsByEmail(email);
    }
}
