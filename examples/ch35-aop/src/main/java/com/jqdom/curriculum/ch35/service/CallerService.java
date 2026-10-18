package com.jqdom.curriculum.ch35.service;

import org.springframework.stereotype.Service;

/** 다른 Bean을 거쳐 부르면 프록시를 지나므로 @Measured가 동작한다. */
@Service
public class CallerService {

    private final GreetingService greetings;

    public CallerService(GreetingService greetings) {
        this.greetings = greetings;
    }

    public String callGreet(String name) {
        return greetings.greet(name);
    }
}
