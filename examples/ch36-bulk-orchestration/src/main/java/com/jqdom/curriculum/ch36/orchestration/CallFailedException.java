package com.jqdom.curriculum.ch36.orchestration;

/** STOP 정책의 Handler가 실패해 전체가 중단되었음을 알린다. 어느 Handler인지 이름을 담는다. */
public class CallFailedException extends RuntimeException {

    private final String handlerKey;

    public CallFailedException(String handlerKey, Throwable cause) {
        super("필수 호출이 실패했습니다: " + handlerKey, cause);
        this.handlerKey = handlerKey;
    }

    public String handlerKey() {
        return handlerKey;
    }
}
