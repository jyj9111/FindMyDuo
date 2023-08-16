package com.idle.fmd.global.error.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 에러 코드를 작성하는 클래스
// 에러코드명( Http Status, "에러 발생 시 메세지" ) 형태로 작성
@RequiredArgsConstructor
@Getter
public enum BusinessExceptionCode {

    // 회원가입 관련 예외
    // 중복된 아이디로 회원가입을 시도할 때 발생하는 예외의 예외코드
    DUPLICATED_USER_ERROR(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),

    // 회원가입 시 비밀번호와 비밀번호 확인 값이 다를 때 발생하는 예외의 예외코드
    PASSWORD_CHECK_ERROR(HttpStatus.BAD_REQUEST, "패스워드와 패스워드 확인이 일치 하지않습니다."),

    // 회원가입 시 인증요청을 보내지 않고 회원가입을 시도했을 때의 예외코드
    NO_EMAIL_AUTH_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "이메일 인증요청을 보내지 않았습니다."),

    // 회원가입 시 이메일 인증코드가 틀렸을 경우의 예외코드
    NOT_VALID_EMAIL_AUTH_CODE_ERROR(HttpStatus.BAD_REQUEST, "올바른 인증코드가 아닙니다."),

    // 회원가입 시 존재하는 이메일로 회원가입을 시도했을 때의 예외코드
    DUPLICATED_EMAIL_ERROR(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),

    // 로그인 관련 예외
    // 로그인시 존재하지 않는 아이디를 입력할시 예외코드
    NOT_EXIST_USER_ERROR(HttpStatus.UNAUTHORIZED, "존재하지 않는 아이디입니다."),

    // 해당 아이디의 비밀번호를 틀리게 입력한 경우 예외코드
    LOGIN_PASSWORD_CHECK_ERROR(HttpStatus.BAD_REQUEST, "해당하는 아이디의 비밀번호를 잘못입력하셨습니다."),

    // OAuth 인증 시 이메일 중복으로 인해 존재하지 않는 유저를 찾게되는 상황에서 발생하는 예외의 예외코드
    // OAuth2SuccessHandler 의 try ~ catch 구문에서 발생하는 예외처리
    UNAVAILABLE_OAUTH_ACCOUNT_ERROR(HttpStatus.BAD_REQUEST, "사용할 수 없는 계정입니다."),

    // 마이페이지 관련 예외
    // 토큰의 계정 정보와 요청 데이터의 계정 정보가 불일치할 때 예외코드
    TOKEN_ACCOUNT_MISMATCH_ERROR(HttpStatus.BAD_REQUEST, "토큰의 계정 정보와 요청 데이터의 계정 정보가 일치하지 않습니다."),

    // 프로필 이미지 저장 실패
    CANNOT_SAVE_IMAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 이미지를 저장할 수 없습니다."),

    // 회원 탈퇴시 프로필 이미지 디렉토리 삭제하는 과정에서 예외 처리
    CANNOT_DELETE_DIRECTORY_ERROR(HttpStatus.BAD_REQUEST, "프로필 이미지 디렉터리 삭제 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
