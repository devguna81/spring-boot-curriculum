package com.jqdom.curriculum.ch34;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ShippingControllerTests {

    @Autowired
    MockMvc mvc;

    @Test
    void feeForStandardOrder() throws Exception {
        mvc.perform(get("/api/shipping/fee").param("method", "standard").param("orderAmount", "30000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.method").value("standard"))
                .andExpect(jsonPath("$.fee").value(3000));
    }

    @Test
    void feeForExpressUsesWeight() throws Exception {
        mvc.perform(get("/api/shipping/fee")
                        .param("method", "express").param("orderAmount", "30000").param("weightKg", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").value(6500));
    }

    @Test
    void methodsAreDiscoveredFromBeans() throws Exception {
        mvc.perform(get("/api/shipping/methods"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"express\",\"pickup\",\"standard\"]"));
    }

    @Test
    void unknownMethodIs400() throws Exception {
        mvc.perform(get("/api/shipping/fee").param("method", "drone").param("orderAmount", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("지원하지 않는 배송 방법입니다: drone")));
    }
}
