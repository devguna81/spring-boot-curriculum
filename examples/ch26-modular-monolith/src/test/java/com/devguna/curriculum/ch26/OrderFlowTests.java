package com.devguna.curriculum.ch26;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devguna.curriculum.ch26.reward.api.RewardApi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class OrderFlowTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    RewardApi rewards;

    @Autowired
    JdbcClient jdbc;

    private ResultActions order(long memberId, String sku, int quantity) throws Exception {
        String body = "{\"memberId\":%d,\"sku\":\"%s\",\"quantity\":%d}".formatted(memberId, sku, quantity);
        return mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private long orderCount() {
        return jdbc.sql("SELECT COUNT(*) FROM orders").query(Long.class).single();
    }

    @Test
    void orderEarnsOnePercentRewardThroughEventWithoutOrderKnowingReward() throws Exception {
        long before = rewards.pointsOf(1);

        // BOOK 15,000원 x 2 = 30,000원 -> 300점
        order(1, "BOOK", 2)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.payable").value(30_000))
                .andExpect(jsonPath("$.orderId").isNumber());

        assertEquals(before + 300, rewards.pointsOf(1));
    }

    @Test
    void rewardsAccumulatePerMember() throws Exception {
        long before = rewards.pointsOf(2);

        order(2, "PEN", 10).andExpect(status().isCreated()); // 12,000원 -> 120점
        order(2, "PEN", 5).andExpect(status().isCreated());  // 6,000원 -> 60점

        assertEquals(before + 180, rewards.pointsOf(2));
    }

    @Test
    void unknownMemberIs404AndLeavesNothingBehind() throws Exception {
        long orders = orderCount();

        order(999, "BOOK", 1).andExpect(status().isNotFound());

        assertEquals(orders, orderCount());
        assertEquals(0, rewards.pointsOf(999));
    }

    @Test
    void unknownProductIs404() throws Exception {
        order(3, "NOPE", 1).andExpect(status().isNotFound());
        assertEquals(0, rewards.pointsOf(3));
    }

    @Test
    void invalidQuantityIs400() throws Exception {
        order(1, "BOOK", 0).andExpect(status().isBadRequest());
    }
}
