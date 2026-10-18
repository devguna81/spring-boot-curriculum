package com.jqdom.curriculum.ch31;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.micrometer.core.instrument.MeterRegistry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ActuatorTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    StockSwitch stock;

    @Autowired
    MeterRegistry registry;

    @AfterEach
    void restoreStock() {
        stock.set(true);
    }

    private void placeOrder() throws Exception {
        mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content("{\"item\":\"book\",\"quantity\":1}"));
    }

    private double count(String result) {
        return registry.get("orders.placed").tag("result", result).counter().count();
    }

    @Test
    void healthIsUpAndShowsEachComponent() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.stock.status").value("UP"))
                .andExpect(jsonPath("$.components.stock.details.reason").value("stock service reachable"))
                .andExpect(jsonPath("$.components.diskSpace.status").value("UP"));
    }

    @Test
    void aDownComponentMakesTheWholeHealthDownWith503() throws Exception {
        stock.set(false);

        mvc.perform(get("/actuator/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.components.stock.status").value("DOWN"));
    }

    @Test
    void customCounterIncreasesPerOutcome() throws Exception {
        double createdBefore = count("created");
        double rejectedBefore = count("rejected");

        placeOrder();
        placeOrder();
        stock.set(false);
        placeOrder();

        assertThat(count("created")).isEqualTo(createdBefore + 2);
        assertThat(count("rejected")).isEqualTo(rejectedBefore + 1);
    }

    @Test
    void metricIsReadableThroughTheMetricsEndpoint() throws Exception {
        placeOrder();

        mvc.perform(get("/actuator/metrics/orders.placed").param("tag", "result:created"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("orders.placed"))
                .andExpect(jsonPath("$.measurements[0].statistic").value("COUNT"));
    }

    @Test
    void httpServerRequestsAreMeasuredAutomatically() throws Exception {
        placeOrder();

        mvc.perform(get("/actuator/metrics/http.server.requests")).andExpect(status().isOk());
    }

    @Test
    void infoShowsConfiguredValues() throws Exception {
        mvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name").value("ch31-logging-actuator"));
    }

    @Test
    void endpointsThatWereNotExposedAreNotFound() throws Exception {
        mvc.perform(get("/actuator/env")).andExpect(status().isNotFound());
        mvc.perform(get("/actuator/beans")).andExpect(status().isNotFound());
    }

    @Test
    void loggerLevelCanBeReadAndChangedAtRuntime() throws Exception {
        String logger = "com.jqdom.curriculum.ch31.OrderController";

        mvc.perform(get("/actuator/loggers/" + logger))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.effectiveLevel").value("DEBUG"));

        try {
            mvc.perform(post("/actuator/loggers/" + logger).contentType(MediaType.APPLICATION_JSON)
                            .content("{\"configuredLevel\":\"WARN\"}"))
                    .andExpect(status().isNoContent());

            mvc.perform(get("/actuator/loggers/" + logger))
                    .andExpect(jsonPath("$.effectiveLevel").value("WARN"));
        } finally {
            mvc.perform(post("/actuator/loggers/" + logger).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"configuredLevel\":null}"));
        }
    }
}
