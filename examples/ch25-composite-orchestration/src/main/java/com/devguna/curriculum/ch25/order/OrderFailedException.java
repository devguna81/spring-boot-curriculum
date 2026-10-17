package com.devguna.curriculum.ch25.order;

/** 주문 흐름의 어느 단계에서 왜 실패했는지를 담는다. 던져질 때 앞 단계의 보상은 이미 끝나 있다. */
public class OrderFailedException extends RuntimeException {

    private final String step;

    public OrderFailedException(String step, RuntimeException cause) {
        super(step + " 단계에서 실패했습니다: " + cause.getMessage(), cause);
        this.step = step;
    }

    public String step() {
        return step;
    }
}
