package com.jqdom.curriculum.ch26.order.api;

public class UnknownProductException extends RuntimeException {

    public UnknownProductException(String sku) {
        super("존재하지 않는 상품입니다: " + sku);
    }
}
