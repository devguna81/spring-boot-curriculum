package com.devguna.curriculum.ch26.order.api;

/** 주문이 만들어졌음을 알리는 사실(event). 다른 모듈이 이 타입만 알면 주문에 반응할 수 있다. */
public record OrderPlaced(long orderId, long memberId, long payable) {
}
