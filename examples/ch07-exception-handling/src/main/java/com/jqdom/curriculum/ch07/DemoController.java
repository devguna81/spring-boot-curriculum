package com.jqdom.curriculum.ch07;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 예상하지 못한 예외가 났을 때 응답이 어떻게 나가는지 보여 주기 위한 엔드포인트. */
@RestController
public class DemoController {

    @GetMapping("/demo/error")
    public String error() {
        throw new IllegalStateException("db password is hunter2");
    }
}
