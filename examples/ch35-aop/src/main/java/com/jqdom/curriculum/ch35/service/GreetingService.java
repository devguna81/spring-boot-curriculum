package com.jqdom.curriculum.ch35.service;

import com.jqdom.curriculum.ch35.aspect.Measured;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    @Measured
    public String greet(String name) {
        return "Hello, " + name + "!";
    }

    @Measured
    public String slow(long millis) throws InterruptedException {
        Thread.sleep(millis);
        return "done";
    }

    @Measured
    public String fail() {
        throw new IllegalStateException("의도한 실패");
    }

    /** 같은 객체 안에서 this로 부르는 메서드. 프록시를 거치지 않아 @Measured가 동작하지 않는다. */
    public String selfCall(String name) {
        return greet(name);
    }

    /** 기록 대상이 아닌 메서드. */
    public String plain() {
        return "plain";
    }
}
