package com.jqdom.curriculum.ch31;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

/** 같은 로그를 줄글 대신 JSON 한 줄로 내보내는 구조화 로그 설정(logstash 형식). */
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(properties = "logging.structured.format.console=logstash")
class StructuredLoggingTests {

    private static final Logger log = LoggerFactory.getLogger("structured.example");

    @Test
    void logLineIsOneJsonObjectWithMdcFields(CapturedOutput output) {
        MDC.put("requestId", "json-1");
        try {
            log.info("order created item=book");
        } finally {
            MDC.remove("requestId");
        }

        String line = output.getOut().lines().filter(l -> l.contains("order created item=book")).findFirst().orElseThrow();
        assertThat(line).startsWith("{").endsWith("}");
        assertThat(line).contains("\"message\":\"order created item=book\"");
        assertThat(line).contains("\"requestId\":\"json-1\"");
        assertThat(line).contains("\"level\":\"INFO\"");
    }
}
