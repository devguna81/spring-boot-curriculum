package com.jqdom.curriculum.ch07;

/** 서비스가 던지는 "예상된 실패". 어떤 오류인지는 ErrorCode가, 사람이 읽을 설명은 message가 담는다. */
public class BusinessException extends RuntimeException {

    private final ErrorCode code;

    public BusinessException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode code() {
        return code;
    }
}
