package com.jqdom.curriculum.ch34;

import org.springframework.stereotype.Component;

/** 빠른 배송: 기본 5,000원에 2kg을 넘는 무게 1kg마다 500원을 더한다. 무료 기준은 없다. */
@Component
class ExpressShipping implements ShippingFeeStrategy {

    static final long BASE_FEE = 5_000;
    static final long PER_KG = 500;
    static final int INCLUDED_KG = 2;

    @Override
    public String key() {
        return "express";
    }

    @Override
    public long fee(ShippingRequest request) {
        int extra = Math.max(0, request.weightKg() - INCLUDED_KG);
        return BASE_FEE + extra * PER_KG;
    }
}
