package com.jqdom.curriculum.ch25;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jqdom.curriculum.ch25.inventory.InMemoryInventoryService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class OrderApiTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    InMemoryInventoryService inventory;

    private ResultActions order(String grade, String sku, int qty, long unitPrice, boolean first) throws Exception {
        String body = """
                {"customerId":"c1","grade":"%s","sku":"%s","quantity":%d,"unitPrice":%d,"firstOrder":%s}
                """.formatted(grade, sku, qty, unitPrice, first);
        return mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void vipOrderGetsSummedAndBestOfDiscount() throws Exception {
        // 60,000원: VIP 6,000 + max(대량 3,000, 첫 주문 2,000) = 9,000 -> 51,000
        order("VIP", "BOOK", 2, 30_000, true)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(60_000))
                .andExpect(jsonPath("$.discount").value(9_000))
                .andExpect(jsonPath("$.payable").value(51_000))
                .andExpect(jsonPath("$.trackingNo").exists());
    }

    @Test
    void outOfStockIs409AndDoesNotChangeStock() throws Exception {
        order("NORMAL", "PEN", 1, 1_000, false)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.step").value("재고"));
        assertEquals(0, inventory.stockOf("PEN"));
    }

    @Test
    void declinedPaymentIs402AndStockIsRestored() throws Exception {
        int before = inventory.stockOf("BOOK");

        order("NORMAL", "BOOK", 1, 2_000_000, false)
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.step").value("결제"));

        assertEquals(before, inventory.stockOf("BOOK"));
    }

    @Test
    void invalidRequestIs400() throws Exception {
        order("NORMAL", "BOOK", 0, 1_000, false).andExpect(status().isBadRequest());
    }
}
