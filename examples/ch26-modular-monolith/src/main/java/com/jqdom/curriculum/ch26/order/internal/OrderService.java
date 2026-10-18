package com.jqdom.curriculum.ch26.order.internal;

import com.jqdom.curriculum.ch26.catalog.api.CatalogApi;
import com.jqdom.curriculum.ch26.member.api.MemberApi;
import com.jqdom.curriculum.ch26.order.api.OrderApi;
import com.jqdom.curriculum.ch26.order.api.OrderPlaced;
import com.jqdom.curriculum.ch26.order.api.UnknownMemberException;
import com.jqdom.curriculum.ch26.order.api.UnknownProductException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 주문 모듈의 구현. 다른 모듈은 member.api, catalog.api의 인터페이스로만 부르고,
 * 주문이 만들어진 사실은 이벤트로 알린다. 누가 듣는지는 모른다.
 * 주문 저장과 이벤트 발행이 한 트랜잭션 안에 있다.
 */
@Service
class OrderService implements OrderApi {

    private final MemberApi members;
    private final CatalogApi catalog;
    private final JdbcClient jdbc;
    private final ApplicationEventPublisher events;

    OrderService(MemberApi members, CatalogApi catalog, JdbcClient jdbc, ApplicationEventPublisher events) {
        this.members = members;
        this.catalog = catalog;
        this.jdbc = jdbc;
        this.events = events;
    }

    @Override
    @Transactional
    public OrderPlaced place(long memberId, String sku, int quantity) {
        if (!members.exists(memberId)) {
            throw new UnknownMemberException(memberId);
        }
        long unitPrice = catalog.priceOf(sku).orElseThrow(() -> new UnknownProductException(sku));
        long payable = unitPrice * quantity;

        GeneratedKeyHolder keys = new GeneratedKeyHolder();
        jdbc.sql("INSERT INTO orders (member_id, sku, quantity, payable) VALUES (:m, :s, :q, :p)")
                .param("m", memberId).param("s", sku).param("q", quantity).param("p", payable)
                .update(keys, "id");
        long orderId = keys.getKey().longValue();

        OrderPlaced placed = new OrderPlaced(orderId, memberId, payable);
        events.publishEvent(placed);
        return placed;
    }
}
