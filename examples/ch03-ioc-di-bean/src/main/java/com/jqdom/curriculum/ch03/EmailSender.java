package com.jqdom.curriculum.ch03;

import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * {@code @Component}는 "이 클래스를 Bean으로 등록해 달라"는 표시다. Bean 이름은 클래스명의 첫 글자를 소문자로 바꾼 emailSender.
 * 같은 타입의 Bean이 여럿일 때 타입만으로 주입하면 {@code @Primary}가 붙은 이 Bean이 선택된다.
 */
@Component
@Primary
@Order(1)
public class EmailSender implements NotificationSender {

    @Override
    public String channel() {
        return "email";
    }

    @Override
    public String send(String message) {
        return "[email] " + message;
    }
}
