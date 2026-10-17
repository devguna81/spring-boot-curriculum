package com.jqdom.curriculum.ch26;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jqdom.curriculum.ch26.order.api.OrderApi;
import com.jqdom.curriculum.ch26.order.api.OrderPlaced;
import com.jqdom.curriculum.ch26.reward.api.RewardApi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionTemplate;

/** 모듈 사이의 일이 어느 트랜잭션에 묶이는지 확인한다. */
@SpringBootTest
class TransactionBoundaryTests {

    @Autowired
    OrderApi orders;

    @Autowired
    RewardApi rewards;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    ApplicationEventPublisher publisher;

    @Autowired
    JdbcClient jdbc;

    private long orderCount() {
        return jdbc.sql("SELECT COUNT(*) FROM orders").query(Long.class).single();
    }

    @Test
    void eventListenerRunsOnlyAfterCommit() {
        long before = rewards.pointsOf(1);

        tx.executeWithoutResult(status -> {
            publisher.publishEvent(new OrderPlaced(1000, 1, 10_000));
            // 아직 커밋 전이므로 적립은 일어나지 않았다.
            assertEquals(before, rewards.pointsOf(1));
        });

        assertEquals(before + 100, rewards.pointsOf(1));
    }

    @Test
    void rolledBackTransactionNeverReachesTheListener() {
        long before = rewards.pointsOf(2);

        assertThrows(IllegalStateException.class, () -> tx.executeWithoutResult(status -> {
            publisher.publishEvent(new OrderPlaced(1001, 2, 50_000));
            throw new IllegalStateException("롤백시킨다");
        }));

        assertEquals(before, rewards.pointsOf(2));
    }

    @Test
    void orderJoinsOuterTransactionSoRollbackRemovesOrderAndSkipsReward() {
        long ordersBefore = orderCount();
        long pointsBefore = rewards.pointsOf(3);

        assertThrows(IllegalStateException.class, () -> tx.executeWithoutResult(status -> {
            orders.place(3, "BOOK", 1);
            throw new IllegalStateException("바깥 트랜잭션을 롤백시킨다");
        }));

        assertEquals(ordersBefore, orderCount());
        assertEquals(pointsBefore, rewards.pointsOf(3));
    }
}
