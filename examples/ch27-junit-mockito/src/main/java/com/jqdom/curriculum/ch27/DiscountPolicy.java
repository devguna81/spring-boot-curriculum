package com.jqdom.curriculum.ch27;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

/** 가입 기간에 따른 할인율(퍼센트). 1년 미만 0, 1년 이상 3년 미만 5, 3년 이상 10. */
public class DiscountPolicy {

    private final Clock clock;

    public DiscountPolicy(Clock clock) {
        this.clock = clock;
    }

    public int ratePercent(User user) {
        int years = Period.between(user.joinedOn(), LocalDate.now(clock)).getYears();
        if (years >= 3) {
            return 10;
        }
        if (years >= 1) {
            return 5;
        }
        return 0;
    }
}
