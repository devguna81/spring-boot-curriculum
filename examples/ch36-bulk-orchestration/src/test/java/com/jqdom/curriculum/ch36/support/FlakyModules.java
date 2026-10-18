package com.jqdom.curriculum.ch36.support;

import java.util.Collection;
import java.util.Map;

import com.jqdom.curriculum.ch36.reward.api.RewardApi;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/** 테스트용 장애 모듈. reward 모듈이 항상 실패하는 상황을 만든다. */
@TestConfiguration
public class FlakyModules {

    @Bean
    @Primary
    RewardApi failingRewards() {
        return new RewardApi() {
            @Override
            public Map<Long, Long> pointsOf(Collection<Long> memberIds) {
                throw new IllegalStateException("reward 모듈 장애");
            }
        };
    }
}
