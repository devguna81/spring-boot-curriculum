package com.jqdom.curriculum.ch35.web;

import java.util.List;

import com.jqdom.curriculum.ch35.aspect.ExecutionLog;
import com.jqdom.curriculum.ch35.aspect.ExecutionRecord;
import com.jqdom.curriculum.ch35.service.GreetingService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class AopController {

    private final GreetingService greetings;
    private final ExecutionLog log;

    AopController(GreetingService greetings, ExecutionLog log) {
        this.greetings = greetings;
        this.log = log;
    }

    @GetMapping("/greet")
    String greet(@RequestParam(defaultValue = "Spring") String name) {
        return greetings.greet(name);
    }

    @GetMapping("/executions")
    List<ExecutionRecord> executions() {
        return log.records();
    }
}
