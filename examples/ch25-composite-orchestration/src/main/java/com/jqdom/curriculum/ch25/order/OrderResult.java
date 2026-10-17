package com.jqdom.curriculum.ch25.order;

public record OrderResult(long orderId, long amount, long discount, long payable,
                          String paymentId, String trackingNo) {
}
