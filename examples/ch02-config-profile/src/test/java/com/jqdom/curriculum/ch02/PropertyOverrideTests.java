package com.jqdom.curriculum.ch02;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 테스트에서는 {@code properties}로 설정을 직접 덮어쓸 수 있다.
 * 실행 환경에서 같은 역할을 하는 것이 명령행 인자(--app.greeting.prefix=...)와 환경 변수(APP_GREETING_PREFIX)다.
 */
@SpringBootTest(properties = "app.greeting.prefix=Override")
class PropertyOverrideTests {

    @Autowired
    private GreetingProperties greeting;

    @Test
    void testPropertyWinsOverApplicationYml() {
        assertEquals("Override", greeting.prefix());
        assertEquals("!", greeting.suffix());
    }
}
