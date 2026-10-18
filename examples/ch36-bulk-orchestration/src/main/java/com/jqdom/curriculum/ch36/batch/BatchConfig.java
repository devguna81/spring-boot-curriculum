package com.jqdom.curriculum.ch36.batch;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BatchConfig {

    /**
     * 배치를 요청 스레드와 분리해 돌리는 단일 스레드. bulk.batch-async=false로 두면
     * 호출한 스레드에서 바로 실행하므로, 테스트가 결과를 기다리지 않고 확인할 수 있다.
     */
    @Bean(destroyMethod = "shutdown")
    ExecutorService batchExecutor(@Value("${bulk.batch-async:true}") boolean async) {
        return async ? Executors.newSingleThreadExecutor() : new CallerRunsExecutorService();
    }
}
