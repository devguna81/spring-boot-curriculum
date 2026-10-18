package com.jqdom.curriculum.ch26.order.api;

public interface OrderApi {

    OrderPlaced place(long memberId, String sku, int quantity);
}
