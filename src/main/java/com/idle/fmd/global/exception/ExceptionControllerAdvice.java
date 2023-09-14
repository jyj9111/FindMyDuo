package com.idle.fmd.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// 프로젝트 전체의 예외를 처리하는 클래스
@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    // BusinessException ( 비즈니스 로직 과정에서 발생하는 예외 ) 를 처리하는 부분
    // enum 클래스의 예외코드로 예외를 구분
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity businessException(BusinessException exception){
        // BusinessException 의 상태코드와 에러 메세지를 받아온다.
        HttpStatus status = exception.getCode().getStatus();
        String message = exception.getCode().getMessage();

        // 로그로 에러내용 출력
        log.error(message);

        // 맵 형태로 응답 바디에 포함 할 내용 생성
        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        // ResponseEntity 의 상태와 바디의 내용을 설정해서 응답으로 리턴
        return ResponseEntity.status(status).body(response);
    }

    // @Valid 를 통한 데이터 유효성 검사가 실패 했을 때 ( 요청 DTO 데이터 유효성 검사 실패 )
    // 모든 요청 Body 데이터의 유효성 검사 실패 시 유효성 검사 실패에 대한 예외를 여기서 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map argumentNotValid(MethodArgumentNotValidException exception){
        // 로그에 에러내용 출력
        log.error(exception.getBindingResult().getFieldErrors().get(0).toString());

        // 예외 발생시 에러 메세지를 받아와서 저장
        String message = exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        Map<String, String> response = new HashMap<>();
        response.put("Message", message);

        return response;
    }
}
