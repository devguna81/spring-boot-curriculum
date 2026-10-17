package com.devguna.curriculum.ch11;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * 테스트 메서드에 @Transactional을 붙이지 않는다.
 * 붙이면 테스트 전체가 하나의 트랜잭션이 되어 서비스의 커밋/롤백 경계를 확인할 수 없다.
 */
@SpringBootTest
class TransactionTests {

    @Autowired
    TransferService service;

    @Autowired
    AccountMapper accounts;

    @Autowired
    AuditMapper audits;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void resetData() {
        jdbc.sql("DELETE FROM audit_log").update();
        jdbc.sql("UPDATE accounts SET balance = 1000 WHERE id = 1").update();
        jdbc.sql("UPDATE accounts SET balance = 500 WHERE id = 2").update();
    }

    private long balance(long id) {
        return accounts.findById(id).balance();
    }

    @Test
    void successfulTransferCommitsBothUpdates() {
        service.transfer(1, 2, 300);

        assertEquals(700, balance(1));
        assertEquals(800, balance(2));
    }

    @Test
    void insufficientFundsLeavesBothBalancesUntouched() {
        assertThrows(InsufficientFundsException.class, () -> service.transfer(1, 2, 5000));

        assertEquals(1000, balance(1));
        assertEquals(500, balance(2));
    }

    @Test
    void failureOnDepositRollsBackTheWithdraw() {
        assertThrows(AccountNotFoundException.class, () -> service.transfer(1, 99, 300));

        assertEquals(1000, balance(1));
    }

    @Test
    void withoutTransactionTheWithdrawStaysCommittedWhenDepositFails() {
        assertThrows(AccountNotFoundException.class, () -> service.transferWithoutTransaction(1, 99, 300));

        // 돈이 사라졌다. 트랜잭션이 없으면 출금은 이미 커밋된 상태다.
        assertEquals(700, balance(1));
    }

    @Test
    void runtimeExceptionAfterBothUpdatesRollsBackBoth() {
        assertThrows(IllegalStateException.class, () -> service.transferThenFail(1, 2, 300));

        assertEquals(1000, balance(1));
        assertEquals(500, balance(2));
    }

    @Test
    void checkedExceptionDoesNotRollBackByDefault() {
        assertThrows(IOException.class, () -> service.transferThenCheckedFailure(1, 2, 300));

        assertEquals(700, balance(1));
        assertEquals(800, balance(2));
    }

    @Test
    void rollbackForMakesCheckedExceptionRollBack() {
        assertThrows(IOException.class, () -> service.transferThenCheckedFailureWithRollbackFor(1, 2, 300));

        assertEquals(1000, balance(1));
        assertEquals(500, balance(2));
    }

    @Test
    void requiresNewAuditSurvivesOuterRollback() {
        assertThrows(IllegalStateException.class, () -> service.transferWithAuditInNewTransaction(1, 2, 300));

        assertEquals(1000, balance(1));
        assertEquals(List.of("이체 시도 from=1 to=2 amount=300"), audits.findAll());
    }

    @Test
    void selfInvocationIgnoresRequiresNewSoAuditIsRolledBackToo() {
        assertThrows(IllegalStateException.class, () -> service.transferWithSelfInvokedAudit(1, 2, 300));

        assertEquals(1000, balance(1));
        assertEquals(List.of(), audits.findAll());
    }

    @Test
    void checkConstraintIsTheLastLineOfDefense() {
        // 애플리케이션 검사를 우회해 음수 잔액을 만들려 하면 데이터베이스가 막는다.
        assertThrows(Exception.class,
                () -> jdbc.sql("UPDATE accounts SET balance = -1 WHERE id = 1").update());
    }
}
