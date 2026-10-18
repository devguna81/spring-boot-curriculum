package com.jqdom.curriculum.ch36.order.internal;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jqdom.curriculum.ch36.common.Chunks;
import com.jqdom.curriculum.ch36.order.api.OrderApi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class OrderService implements OrderApi {

    private final OrderMapper mapper;
    private final int inChunkSize;

    OrderService(OrderMapper mapper, @Value("${bulk.in-chunk-size}") int inChunkSize) {
        this.mapper = mapper;
        this.inChunkSize = inChunkSize;
    }

    @Override
    public Map<Long, Long> orderCountsOf(Collection<Long> memberIds) {
        Map<Long, Long> result = new LinkedHashMap<>();
        for (OrderCountRow row : Chunks.lookup(memberIds, inChunkSize, mapper::countByMemberIds)) {
            result.merge(row.memberId(), row.orderCount(), Long::sum);
        }
        return result;
    }
}
