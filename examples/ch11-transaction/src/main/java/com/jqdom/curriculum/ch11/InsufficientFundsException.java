package com.jqdom.curriculum.ch11;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(long accountId, long amount) {
        super("잔액이 부족하거나 계좌가 없습니다. accountId=" + accountId + ", amount=" + amount);
    }
}
