package com.idle.fmd.global.error.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 에러 코드를 작성하는 클래스
// 에러코드명( Http Status, "에러 발생 시 메세지" ) 형태로 작성
@RequiredArgsConstructor
@Getter
public enum BusinessExceptionCode {
    // 로그인 관련 예외

    // 로그인시 존재하지 않는 아이디를 입력할시 예외코드
    NOT_EXIST_USER_ERROR(HttpStatus.UNAUTHORIZED, "존재하지 않는 아이디입니다."),

    // 해당 아이디의 비밀번호를 틀리게 입력한 경우 예외코드
    LOGIN_PASSWORD_CHECK_ERROR(HttpStatus.BAD_REQUEST, "해당하는 아이디의 비밀번호를 잘못입력하셨습니다.");



    private final HttpStatus status;
    private final String message;
}
