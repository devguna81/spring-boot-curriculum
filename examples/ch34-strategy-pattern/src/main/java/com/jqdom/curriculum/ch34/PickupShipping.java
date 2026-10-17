package com.jqdom.curriculum.ch34;

import org.springframework.stereotype.Component;

/** 매장 수령: 배송비가 없다. */
@Component
class PickupShipping implements ShippingFeeStrategy {

    @Override
    public String key() {
        return "pickup";
    }

    @Override
    public long fee(ShippingRequest request) {
        return 0;
    }
}
