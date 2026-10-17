package com.devguna.curriculum.ch17;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 예상하지 못한 예외가 났을 때 응답이 어떻게 나가는지 보여 주기 위한 엔드포인트. */
/** 문서에 보이지 않게 @Hidden을 붙였다. 내부 확인용 엔드포인트는 공개 문서에서 뺀다. */
@Hidden
@RestController
public class DemoController {

    @GetMapping("/demo/error")
    public String error() {
        throw new IllegalStateException("db password is hunter2");
    }
}
