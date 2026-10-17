package com.jqdom.curriculum.ch36.orchestration.handler;

import com.jqdom.curriculum.ch36.order.api.OrderApi;
import com.jqdom.curriculum.ch36.orchestration.CallContext;
import com.jqdom.curriculum.ch36.orchestration.CallHandler;
import com.jqdom.curriculum.ch36.orchestration.SummaryDraft;

import org.springframework.stereotype.Component;

@Component
class OrderHandler implements CallHandler {

    private final OrderApi orders;

    OrderHandler(OrderApi orders) {
        this.orders = orders;
    }

    @Override
    public String key() {
        return "order";
    }

    @Override
    public void execute(CallContext context, SummaryDraft draft) {
        draft.putOrderCounts(orders.orderCountsOf(context.memberIds()));
    }
}
