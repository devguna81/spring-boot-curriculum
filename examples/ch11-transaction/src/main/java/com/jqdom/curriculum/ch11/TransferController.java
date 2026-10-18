package com.jqdom.curriculum.ch11;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController {

    public record TransferRequest(long from, long to, long amount) {
    }

    private final TransferService service;
    private final AccountMapper accounts;

    public TransferController(TransferService service, AccountMapper accounts) {
        this.service = service;
        this.accounts = accounts;
    }

    @PostMapping("/transfers")
    public Map<String, Account> transfer(@RequestBody TransferRequest request) {
        service.transfer(request.from(), request.to(), request.amount());
        return Map.of("from", accounts.findById(request.from()), "to", accounts.findById(request.to()));
    }

    @GetMapping("/accounts/{id}")
    public Account account(@PathVariable long id) {
        Account account = accounts.findById(id);
        if (account == null) {
            throw new AccountNotFoundException(id);
        }
        return account;
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> insufficient(InsufficientFundsException e) {
        return Map.of("code", "INSUFFICIENT_FUNDS", "message", e.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(AccountNotFoundException e) {
        return Map.of("code", "ACCOUNT_NOT_FOUND", "message", e.getMessage());
    }
}
