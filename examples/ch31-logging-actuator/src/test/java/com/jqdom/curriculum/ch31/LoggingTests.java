package com.jqdom.curriculum.ch31;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** 콘솔에 실제로 찍힌 로그 줄을 잡아서 형식과 MDC를 확인한다. */
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class LoggingTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    StockSwitch stock;

    private void order(String requestId) throws Exception {
        var request = post("/orders").contentType(MediaType.APPLICATION_JSON).content("{\"item\":\"book\",\"quantity\":2}");
        if (requestId != null) {
            request.header("X-Request-Id", requestId);
        }
        mvc.perform(request).andExpect(status().is2xxSuccessful());
    }

    @Test
    void requestIdFromTheHeaderAppearsOnEveryLogLineOfThatRequest(CapturedOutput output) throws Exception {
        order("abc-123");

        assertThat(output.getOut()).contains("DEBUG [abc-123]").contains("order requested item=book quantity=2");
        assertThat(output.getOut()).contains("INFO  [abc-123]").contains("order created item=book quantity=2");
    }

    @Test
    void aRequestIdIsGeneratedAndReturnedWhenTheClientSendsNone() throws Exception {
        var result = mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"item\":\"pen\",\"quantity\":1}"))
                .andExpect(header().exists("X-Request-Id"))
                .andReturn();

        String generated = result.getResponse().getHeader("X-Request-Id");
        assertThat(generated).hasSize(8);
    }

    @Test
    void unsafeHeaderCharactersAreStrippedBeforeTheyReachTheLog(CapturedOutput output) throws Exception {
        order("evil\r\nINFO forged line");

        assertThat(output.getOut()).doesNotContain("evil\r\nINFO forged line");
        assertThat(output.getOut()).contains("[evilINFOforgedline]");
    }

    @Test
    void mdcIsClearedAfterTheRequest(CapturedOutput output) throws Exception {
        order("req-one");
        org.slf4j.LoggerFactory.getLogger("outside.request").info("after request");

        assertThat(output.getOut()).contains("[none] outside.request - after request");
    }

    @Test
    void warnLevelLogIsWrittenWhenTheOrderIsRejected(CapturedOutput output) throws Exception {
        stock.set(false);
        try {
            mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).header("X-Request-Id", "rej-1")
                    .content("{\"item\":\"book\",\"quantity\":1}")).andExpect(status().isServiceUnavailable());
        } finally {
            stock.set(true);
        }

        assertThat(output.getOut()).contains("WARN  [rej-1]").contains("order rejected: stock service unavailable");
    }
}
