package com.jqdom.curriculum.ch34;

/**
 * 배송 방법 하나의 계산 규칙(전략). key()는 요청에서 방법을 고르는 이름이다.
 * 새 방법은 이 인터페이스를 구현한 Bean을 하나 더 만들면 계산기 수정 없이 편입된다.
 */
public interface ShippingFeeStrategy {

    String key();

    long fee(ShippingRequest request);
}
