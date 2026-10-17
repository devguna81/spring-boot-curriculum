package com.devguna.curriculum.ch32;

import java.util.Map;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 컨테이너를 다시 띄워도 방문 수가 이어지는지(데이터가 컨테이너 밖 볼륨에 남는지) 보여 주려는 최소 기능. */
@RestController
class VisitController {

    private static final String NAME = "home";

    private final JdbcClient jdbc;

    VisitController(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/hello")
    Map<String, String> hello() {
        return Map.of("message", "hello from container");
    }

    @GetMapping("/visits")
    @Transactional
    Map<String, Long> visit() {
        int updated = jdbc.sql("UPDATE visit_counter SET total = total + 1 WHERE name = :n").param("n", NAME).update();
        if (updated == 0) {
            jdbc.sql("INSERT INTO visit_counter (name, total) VALUES (:n, 1)").param("n", NAME).update();
        }
        Long total = jdbc.sql("SELECT total FROM visit_counter WHERE name = :n").param("n", NAME).query(Long.class).single();
        return Map.of("visits", total);
    }
}
