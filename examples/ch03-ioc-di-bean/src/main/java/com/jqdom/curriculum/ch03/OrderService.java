package com.jqdom.curriculum.ch03;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 생성자 주입. 의존 객체를 생성자 인자로 받으므로
 * (1) 필드를 final로 둘 수 있고 (2) Spring 없이 {@code new OrderService(...)}로 테스트할 수 있다.
 * 생성자가 하나뿐이면 {@code @Autowired}는 생략한다.
 */
@Service
public class OrderService {

    private final NotificationSender sender;
    private final NotificationSender urgentSender;
    private final List<NotificationSender> allSenders;

    public OrderService(
            NotificationSender sender,                              // @Primary인 EmailSender
            @Qualifier("smsSender") NotificationSender urgentSender, // 이름으로 지정
            List<NotificationSender> allSenders) {                  // 해당 타입의 Bean 전부
        this.sender = sender;
        this.urgentSender = urgentSender;
        this.allSenders = allSenders;
    }

    public String place(String item, boolean urgent) {
        NotificationSender target = urgent ? urgentSender : sender;
        return target.send("order placed: " + item);
    }

    public List<String> channels() {
        return allSenders.stream().map(NotificationSender::channel).toList();
    }
}
