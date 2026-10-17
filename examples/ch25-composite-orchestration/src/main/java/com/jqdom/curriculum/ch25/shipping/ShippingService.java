package com.jqdom.curriculum.ch25.shipping;

public interface ShippingService {

    /** 배송을 예약하고 송장 번호를 돌려준다. */
    String schedule(long orderId, String customerId);
}
