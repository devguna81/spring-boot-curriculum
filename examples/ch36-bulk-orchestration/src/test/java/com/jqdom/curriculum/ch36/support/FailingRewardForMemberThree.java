package com.jqdom.curriculum.ch36.support;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jqdom.curriculum.ch36.orchestration.CallRule;
import com.jqdom.curriculum.ch36.orchestration.CallRuleSource;
import com.jqdom.curriculum.ch36.orchestration.FailPolicy;
import com.jqdom.curriculum.ch36.reward.api.RewardApi;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/** 회원 3이 들어 있는 묶음에서만 reward가 실패하고, reward를 STOP으로 바꿔 묶음 전체를 실패시킨다. */
@TestConfiguration
public class FailingRewardForMemberThree {

    @Bean
    @Primary
    RewardApi rewardsFailingOnThree() {
        return new RewardApi() {
            @Override
            public Map<Long, Long> pointsOf(Collection<Long> memberIds) {
                if (memberIds.contains(3L)) {
                    throw new IllegalStateException("회원 3 처리 실패");
                }
                return Map.of();
            }
        };
    }

    @Bean
    @Primary
    CallRuleSource stopOnReward() {
        return () -> List.of(
                new CallRule("member", 1, FailPolicy.STOP),
                new CallRule("reward", 2, FailPolicy.STOP),
                new CallRule("order", 3, FailPolicy.WARNING));
    }
}
