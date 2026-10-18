package com.jqdom.curriculum.ch36;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 실패 정책 CONTINUE와 쓰기가 섞일 때의 트랜잭션 경계.
 * 바깥 트랜잭션에 참여한 안쪽 호출이 예외를 던지면, 바깥에서 그 예외를 잡아도 롤백 표시가 남아 커밋이 실패한다.
 * 안쪽을 새 트랜잭션(REQUIRES_NEW)으로 분리하면 안쪽만 취소되고 바깥은 커밋된다.
 */
@SpringBootTest
@Import(TransactionPolicyTests.Config.class)
class TransactionPolicyTests {

    @Autowired
    Outer outer;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void clean() {
        jdbc.sql("DELETE FROM batch_job").update();
    }

    private long jobCount() {
        return jdbc.sql("SELECT COUNT(*) FROM batch_job").query(Long.class).single();
    }

    @Test
    void participatingFailureMarksTheWholeTransactionRollbackOnly() {
        assertThrows(UnexpectedRollbackException.class, () -> outer.continueAfter(false));
        assertEquals(0, jobCount(), "바깥 쓰기도 함께 취소된다");
    }

    @Test
    void requiresNewIsolatesTheFailure() {
        outer.continueAfter(true);
        assertEquals(1, jobCount(), "바깥 쓰기만 커밋되고 안쪽 쓰기는 취소된다");
    }

    @TestConfiguration
    static class Config {

        @Bean
        Outer outer(JdbcClient jdbc, InnerParticipating participating, InnerIsolated isolated) {
            return new Outer(jdbc, participating, isolated);
        }

        @Bean
        InnerParticipating participating(JdbcClient jdbc) {
            return new InnerParticipating(jdbc);
        }

        @Bean
        InnerIsolated isolated(JdbcClient jdbc) {
            return new InnerIsolated(jdbc);
        }
    }

    static class Outer {
        private final JdbcClient jdbc;
        private final InnerParticipating participating;
        private final InnerIsolated isolated;

        Outer(JdbcClient jdbc, InnerParticipating participating, InnerIsolated isolated) {
            this.jdbc = jdbc;
            this.participating = participating;
            this.isolated = isolated;
        }

        @Transactional
        public void continueAfter(boolean isolate) {
            jdbc.sql("INSERT INTO batch_job (status, processed, failed) VALUES ('OUTER', 0, 0)").update();
            try {
                if (isolate) {
                    isolated.writeAndFail();
                } else {
                    participating.writeAndFail();
                }
            } catch (IllegalStateException e) {
                // CONTINUE 정책처럼 실패를 기록만 하고 계속 진행한다.
            }
        }
    }

    @Service
    static class InnerParticipating {
        private final JdbcClient jdbc;

        InnerParticipating(JdbcClient jdbc) {
            this.jdbc = jdbc;
        }

        @Transactional
        public void writeAndFail() {
            jdbc.sql("INSERT INTO batch_job (status, processed, failed) VALUES ('INNER', 0, 0)").update();
            throw new IllegalStateException("안쪽 실패");
        }
    }

    static class InnerIsolated {
        private final JdbcClient jdbc;

        InnerIsolated(JdbcClient jdbc) {
            this.jdbc = jdbc;
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void writeAndFail() {
            jdbc.sql("INSERT INTO batch_job (status, processed, failed) VALUES ('INNER', 0, 0)").update();
            throw new IllegalStateException("안쪽 실패");
        }
    }
}
