package com.devguna.curriculum.ch26.reward.internal;

import com.devguna.curriculum.ch26.order.api.OrderPlaced;
import com.devguna.curriculum.ch26.reward.api.RewardApi;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 주문 모듈의 사실(OrderPlaced)에 반응해 적립금을 쌓는다. 이 모듈은 order.api만 알고 order.internal은 모른다.
 */
@Component
class RewardService implements RewardApi {

    private final JdbcClient jdbc;

    RewardService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 주문 트랜잭션이 커밋된 뒤에만 실행된다. 롤백되면 호출되지 않는다.
     * TransactionSynchronization.afterCommit의 Javadoc은 이 시점의 데이터 접근이 이미 끝난 원래 트랜잭션에 걸릴 수 있으니
     * 트랜잭션이 필요한 작업은 REQUIRES_NEW로 실행하라고 안내한다. 이 예제에서는 어노테이션을 빼도 값이 반영되는 것을
     * 확인했지만(원인은 확인하지 않았다), 안내를 따라 새 트랜잭션을 명시한다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void onOrderPlaced(OrderPlaced event) {
        long points = event.payable() / 100;
        int updated = jdbc.sql("UPDATE reward SET points = points + :p WHERE member_id = :m")
                .param("p", points).param("m", event.memberId()).update();
        if (updated == 0) {
            jdbc.sql("INSERT INTO reward (member_id, points) VALUES (:m, :p)")
                    .param("m", event.memberId()).param("p", points).update();
        }
    }

    @Override
    public long pointsOf(long memberId) {
        return jdbc.sql("SELECT points FROM reward WHERE member_id = :m")
                .param("m", memberId).query(Long.class).optional().orElse(0L);
    }
}
