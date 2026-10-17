package com.devguna.curriculum.ch23.notification;

import com.devguna.curriculum.ch23.domain.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogWelcomeNotifier implements WelcomeNotifier {

    private static final Logger log = LoggerFactory.getLogger(LogWelcomeNotifier.class);

    @Override
    public void welcome(User user) {
        log.info("welcome {}", user.email());
    }
}
