package com.devguna.curriculum.ch03;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Bean 이름은 smsSender. 이름이나 {@code @Qualifier("smsSender")}로 지정해서 주입받을 수 있다. */
@Component
@Order(2)
public class SmsSender implements NotificationSender {

    @Override
    public String channel() {
        return "sms";
    }

    @Override
    public String send(String message) {
        return "[sms] " + message;
    }
}
