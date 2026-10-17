package com.devguna.curriculum.ch25.inventory;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String sku) {
        super("재고가 부족합니다: " + sku);
    }
}
