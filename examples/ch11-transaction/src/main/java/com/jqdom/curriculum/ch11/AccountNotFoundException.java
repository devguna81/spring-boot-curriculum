package com.jqdom.curriculum.ch11;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(long accountId) {
        super("계좌를 찾을 수 없습니다. accountId=" + accountId);
    }
}
