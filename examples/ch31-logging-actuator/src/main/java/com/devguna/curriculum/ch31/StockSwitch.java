package com.devguna.curriculum.ch31;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

/** 재고 시스템이 살아 있는지를 나타내는 스위치. 실제라면 외부 시스템에 연결해 확인하는 자리다. */
@Component
public class StockSwitch {

    private final AtomicBoolean available = new AtomicBoolean(true);

    public boolean available() {
        return available.get();
    }

    public void set(boolean value) {
        available.set(value);
    }
}
