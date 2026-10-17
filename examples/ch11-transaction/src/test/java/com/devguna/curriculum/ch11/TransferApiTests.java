package com.devguna.curriculum.ch11;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TransferApiTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void resetData() {
        jdbc.sql("UPDATE accounts SET balance = 1000 WHERE id = 1").update();
        jdbc.sql("UPDATE accounts SET balance = 500 WHERE id = 2").update();
    }

    private String body(long from, long to, long amount) {
        return "{\"from\":%d,\"to\":%d,\"amount\":%d}".formatted(from, to, amount);
    }

    @Test
    void transferReturnsUpdatedBalances() throws Exception {
        mockMvc.perform(post("/transfers").contentType(MediaType.APPLICATION_JSON).content(body(1, 2, 200)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from.balance").value(800))
                .andExpect(jsonPath("$.to.balance").value(700));
    }

    @Test
    void insufficientFundsReturns409AndKeepsBalance() throws Exception {
        mockMvc.perform(post("/transfers").contentType(MediaType.APPLICATION_JSON).content(body(1, 2, 9999)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_FUNDS"));
        mockMvc.perform(get("/accounts/1")).andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void unknownTargetReturns404AndRollsBackWithdraw() throws Exception {
        mockMvc.perform(post("/transfers").contentType(MediaType.APPLICATION_JSON).content(body(1, 99, 100)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
        mockMvc.perform(get("/accounts/1")).andExpect(jsonPath("$.balance").value(1000));
    }
}
