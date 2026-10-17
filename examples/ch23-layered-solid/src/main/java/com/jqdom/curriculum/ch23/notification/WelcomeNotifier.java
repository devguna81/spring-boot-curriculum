package com.jqdom.curriculum.ch23.notification;

import com.jqdom.curriculum.ch23.domain.User;

/** 가입 알림을 보내는 방법(로그, 메일, 메신저)을 서비스가 알 필요가 없도록 인터페이스로 둔다. */
public interface WelcomeNotifier {

    void welcome(User user);
}
