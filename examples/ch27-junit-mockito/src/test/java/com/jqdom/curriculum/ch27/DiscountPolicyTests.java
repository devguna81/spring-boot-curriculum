package com.jqdom.curriculum.ch27;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/** 협력 객체가 없는 순수 로직은 Mockito 없이 값만 바꿔 가며 검사한다. */
class DiscountPolicyTests {

    private static final Clock TODAY = Clock.fixed(Instant.parse("2026-10-17T00:00:00Z"), ZoneOffset.UTC);

    private final DiscountPolicy policy = new DiscountPolicy(TODAY);

    private User joinedOn(String date) {
        return new User(1, "kim", "kim@example.com", LocalDate.parse(date));
    }

    @ParameterizedTest(name = "{0}에 가입하면 {1}%")
    @CsvSource({
            "2026-10-17, 0",   // 오늘 가입
            "2025-10-18, 0",   // 1년이 되기 하루 전
            "2025-10-17, 5",   // 정확히 1년
            "2023-10-18, 5",   // 3년이 되기 하루 전
            "2023-10-17, 10",  // 정확히 3년
            "2015-01-01, 10"
    })
    void rateDependsOnMembershipYears(String joined, int expected) {
        assertThat(policy.ratePercent(joinedOn(joined))).isEqualTo(expected);
    }

    @Test
    @DisplayName("같은 사용자도 시계가 달라지면 결과가 달라진다")
    void clockDecidesToday() {
        Clock later = Clock.fixed(Instant.parse("2029-10-17T00:00:00Z"), ZoneOffset.UTC);

        assertThat(new DiscountPolicy(later).ratePercent(joinedOn("2026-10-17"))).isEqualTo(10);
        assertThat(policy.ratePercent(joinedOn("2026-10-17"))).isZero();
    }
}
