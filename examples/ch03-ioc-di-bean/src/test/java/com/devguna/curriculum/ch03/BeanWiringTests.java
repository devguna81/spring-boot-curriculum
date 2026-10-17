package com.devguna.curriculum.ch03;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;

import com.devguna.curriculum.outside.OutsideComponent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BeanWiringTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    @Test
    void primaryAndQualifierSelectSender() throws Exception {
        mockMvc.perform(get("/orders").param("item", "book"))
                .andExpect(status().isOk())
                .andExpect(content().string("[email] order placed: book"));

        mockMvc.perform(get("/orders").param("item", "book").param("urgent", "true"))
                .andExpect(content().string("[sms] order placed: book"));
    }

    @Test
    void listInjectionCollectsAllImplementationsInOrder() throws Exception {
        mockMvc.perform(get("/senders"))
                .andExpect(jsonPath("$", contains("email", "sms")));
    }

    @Test
    void beanNamesFollowNamingRules() throws Exception {
        // 클래스명 첫 글자를 소문자로 바꾼 이름, @Bean 메서드는 메서드 이름
        mockMvc.perform(get("/beans"))
                .andExpect(jsonPath("$", hasItems("emailSender", "smsSender", "orderService", "systemClock", "appConfig")));
    }

    @Test
    void beanMethodRegistersLibraryClass() {
        assertTrue(context.containsBean("systemClock"));
        assertInstanceOf(Clock.class, context.getBean("systemClock"));
    }

    @Test
    void singletonIsSharedByDefault() {
        assertSame(context.getBean(OrderService.class), context.getBean(OrderService.class));
    }

    @Test
    void componentOutsideScanPackageIsNotRegistered() {
        assertEquals(0, context.getBeanNamesForType(OutsideComponent.class).length);
    }

    @Test
    void lifecycleCallbacksRanAtStartup() {
        assertEquals(java.util.List.of("constructor", "postConstruct"), context.getBean(LifecycleRecorder.class).events());
    }

    @Test
    void differentInstancesAreDistinctBeans() {
        assertNotSame(context.getBean("emailSender"), context.getBean("smsSender"));
    }
}
