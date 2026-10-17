package com.jqdom.curriculum.ch25.shipping;

import org.springframework.stereotype.Service;

@Service
public class InMemoryShippingService implements ShippingService {

    @Override
    public String schedule(long orderId, String customerId) {
        return "TRK-" + orderId;
    }
}
