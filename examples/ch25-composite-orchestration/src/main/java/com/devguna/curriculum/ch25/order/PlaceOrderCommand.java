package com.devguna.curriculum.ch25.order;

import com.devguna.curriculum.ch25.pricing.Grade;

public record PlaceOrderCommand(String customerId, Grade grade, String sku, int quantity,
                                long unitPrice, boolean firstOrder) {
}
