package com.jqdom.curriculum.ch03;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/** 생성자 주입 덕분에 Spring 컨테이너 없이 가짜 구현을 넣어 테스트할 수 있다. */
class OrderServiceUnitTests {

    private static NotificationSender fake(String channel) {
        return new NotificationSender() {
            @Override
            public String channel() {
                return channel;
            }

            @Override
            public String send(String message) {
                return channel + ":" + message;
            }
        };
    }

    @Test
    void usesDefaultSenderUnlessUrgent() {
        NotificationSender normal = fake("normal");
        NotificationSender urgent = fake("urgent");
        OrderService service = new OrderService(normal, urgent, List.of(normal, urgent));

        assertEquals("normal:order placed: book", service.place("book", false));
        assertEquals("urgent:order placed: book", service.place("book", true));
        assertEquals(List.of("normal", "urgent"), service.channels());
    }
}
