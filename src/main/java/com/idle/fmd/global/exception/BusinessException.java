package com.idle.fmd.global.exception;

import lombok.Getter;

// 비즈니스 로직 ( 서비스 클래스의 로직 ) 에서 발생하는 모든 예외에 대한 클래스
public class BusinessException extends RuntimeException{
    @Getter
    private final BusinessExceptionCode code;

    public BusinessException(BusinessExceptionCode code){
        this.code = code;
    }
}
