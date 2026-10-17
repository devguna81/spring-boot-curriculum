package com.jqdom.curriculum.ch11;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** 같은 이체를 트랜잭션 설정만 바꿔 여러 방식으로 구현해 결과를 비교한다. */
@Service
public class TransferService {

    private final AccountMapper accounts;
    private final AuditService audit;

    public TransferService(AccountMapper accounts, AuditService audit) {
        this.accounts = accounts;
        this.audit = audit;
    }

    private void move(long from, long to, long amount) {
        if (accounts.withdraw(from, amount) == 0) {
            throw new InsufficientFundsException(from, amount);
        }
        if (accounts.deposit(to, amount) == 0) {
            throw new AccountNotFoundException(to);
        }
    }

    /** 정상 경로. 두 UPDATE가 하나의 트랜잭션이라 함께 성공하거나 함께 취소된다. */
    @Transactional
    public void transfer(long from, long to, long amount) {
        move(from, to, amount);
    }

    /** 트랜잭션이 없으면 출금은 이미 커밋되고, 입금 실패는 되돌려지지 않는다. */
    public void transferWithoutTransaction(long from, long to, long amount) {
        move(from, to, amount);
    }

    /** 두 UPDATE가 모두 끝난 뒤 런타임 예외가 나면 둘 다 롤백된다. */
    @Transactional
    public void transferThenFail(long from, long to, long amount) {
        move(from, to, amount);
        throw new IllegalStateException("이체 후 후속 작업 실패");
    }

    /** 체크 예외는 기본 설정에서 롤백하지 않는다. 커밋되어 돈이 이동한 채로 남는다. */
    @Transactional
    public void transferThenCheckedFailure(long from, long to, long amount) throws IOException {
        move(from, to, amount);
        throw new IOException("외부 시스템 통지 실패");
    }

    /** rollbackFor로 체크 예외도 롤백 대상에 넣는다. */
    @Transactional(rollbackFor = Exception.class)
    public void transferThenCheckedFailureWithRollbackFor(long from, long to, long amount) throws IOException {
        move(from, to, amount);
        throw new IOException("외부 시스템 통지 실패");
    }

    /** 다른 Bean의 REQUIRES_NEW 메서드를 호출한다. 감사 기록은 이체가 롤백되어도 남는다. */
    @Transactional
    public void transferWithAuditInNewTransaction(long from, long to, long amount) {
        audit.recordInNewTransaction("이체 시도 from=" + from + " to=" + to + " amount=" + amount);
        move(from, to, amount);
        throw new IllegalStateException("롤백을 일으키기 위한 실패");
    }

    /** 같은 Bean 안에서 호출하는 REQUIRES_NEW는 프록시를 거치지 않아 무시된다. 감사 기록도 함께 롤백된다. */
    @Transactional
    public void transferWithSelfInvokedAudit(long from, long to, long amount) {
        auditInNewTransaction("이체 시도 from=" + from + " to=" + to + " amount=" + amount);
        move(from, to, amount);
        throw new IllegalStateException("롤백을 일으키기 위한 실패");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditInNewTransaction(String message) {
        // 프록시를 거치지 않은 내부 호출이라 이 애노테이션은 적용되지 않는다.
        audit.recordJoiningCaller(message);
    }
}
