package com.devguna.curriculum.ch24;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/** 웹 -> 응용 -> 진짜 HTTP 어댑터 -> 가짜 외부 서버까지 이어서 확인한다. */
@SpringBootTest
@AutoConfigureMockMvc
class MemberApiTests {

    private static FakeAddressServer external;

    @BeforeAll
    static void startExternal() throws Exception {
        external = new FakeAddressServer();
    }

    @AfterAll
    static void stopExternal() {
        external.close();
    }

    @DynamicPropertySource
    static void externalProperties(DynamicPropertyRegistry registry) throws Exception {
        // 컨텍스트가 만들어지기 전에 속성을 읽으므로 서버를 여기서 먼저 보장한다.
        if (external == null) {
            external = new FakeAddressServer();
        }
        registry.add("app.address-api.base-url", () -> external.baseUrl());
    }

    @AfterEach
    void reset() {
        external.forcedStatus.set(0);
    }

    @Autowired
    MockMvc mvc;

    private static String json(String name, String postalCode) {
        return "{\"name\":\"" + name + "\",\"postalCode\":\"" + postalCode + "\"}";
    }

    @Test
    void registerReturnsAddressTranslatedFromExternalApi() throws Exception {
        String location = mvc.perform(post("/api/members").contentType(MediaType.APPLICATION_JSON)
                        .content(json("kim", "06236")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address.city").value("서울"))
                .andExpect(jsonPath("$.address.district").value("강남구"))
                .andExpect(jsonPath("$.address.street").value("테헤란로"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("/api/members/")))
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("kim"));
    }

    @Test
    void unknownPostalCodeIs400() throws Exception {
        mvc.perform(post("/api/members").contentType(MediaType.APPLICATION_JSON).content(json("kim", "99999")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("존재하지 않는 우편번호입니다: 99999"));
    }

    @Test
    void malformedPostalCodeIs400WithoutCallingExternalApi() throws Exception {
        int before = external.requestedPaths.size();

        mvc.perform(post("/api/members").contentType(MediaType.APPLICATION_JSON).content(json("kim", "abc")))
                .andExpect(status().isBadRequest());

        org.junit.jupiter.api.Assertions.assertEquals(before, external.requestedPaths.size());
    }

    @Test
    void externalFailureIs503() throws Exception {
        external.forcedStatus.set(500);

        mvc.perform(post("/api/members").contentType(MediaType.APPLICATION_JSON).content(json("kim", "06236")))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("주소 확인 서비스를 잠시 사용할 수 없습니다"));
    }

    @Test
    void unknownMemberIs404() throws Exception {
        mvc.perform(get("/api/members/9999")).andExpect(status().isNotFound());
    }
}
