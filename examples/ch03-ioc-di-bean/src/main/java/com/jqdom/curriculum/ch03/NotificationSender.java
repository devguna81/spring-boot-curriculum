package com.jqdom.curriculum.ch03;

/** 알림 전송 방식의 추상화. 호출하는 쪽은 구현 클래스를 알 필요가 없다. */
public interface NotificationSender {

    String channel();

    String send(String message);
}
