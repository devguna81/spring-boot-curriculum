package com.devguna.curriculum.ch25;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.devguna.curriculum.ch25.pricing.BulkAmountRule;
import com.devguna.curriculum.ch25.pricing.CompositeDiscountRule;
import com.devguna.curriculum.ch25.pricing.DiscountRule;
import com.devguna.curriculum.ch25.pricing.FirstOrderRule;
import com.devguna.curriculum.ch25.pricing.Grade;
import com.devguna.curriculum.ch25.pricing.PricingContext;
import com.devguna.curriculum.ch25.pricing.VipRateRule;

import org.junit.jupiter.api.Test;

class CompositeDiscountRuleTests {

    /** 운영 설정과 같은 모양: VIP는 항상 더하고, 대량/첫 주문은 큰 쪽 하나. */
    private final DiscountRule tree = CompositeDiscountRule.sumOf(
            new VipRateRule(),
            CompositeDiscountRule.bestOf(new BulkAmountRule(), new FirstOrderRule()));

    @Test
    void leafRulesDecideOnTheirOwn() {
        assertEquals(6_000, new VipRateRule().discount(new PricingContext(Grade.VIP, 60_000, false)));
        assertEquals(0, new VipRateRule().discount(new PricingContext(Grade.NORMAL, 60_000, false)));
        assertEquals(3_000, new BulkAmountRule().discount(new PricingContext(Grade.NORMAL, 50_000, false)));
        assertEquals(0, new BulkAmountRule().discount(new PricingContext(Grade.NORMAL, 49_999, false)));
        assertEquals(2_000, new FirstOrderRule().discount(new PricingContext(Grade.NORMAL, 1_000, true)));
    }

    @Test
    void sumAddsVipAndTheBestOfTheOthers() {
        // VIP 6,000 + max(대량 3,000, 첫 주문 2,000) = 9,000
        assertEquals(9_000, tree.discount(new PricingContext(Grade.VIP, 60_000, true)));
    }

    @Test
    void bestOfPicksOnlyOneNotBoth() {
        // 일반 등급이면 max(3,000, 2,000) = 3,000. 5,000이 아니다.
        assertEquals(3_000, tree.discount(new PricingContext(Grade.NORMAL, 60_000, true)));
    }

    @Test
    void noMatchingRuleMeansNoDiscount() {
        assertEquals(0, tree.discount(new PricingContext(Grade.NORMAL, 10_000, false)));
    }

    @Test
    void sumIsCappedAtOrderAmount() {
        // 1,000원 주문에 VIP 100 + 첫 주문 2,000 = 2,100이지만 주문 금액 1,000까지만.
        assertEquals(1_000, tree.discount(new PricingContext(Grade.VIP, 1_000, true)));
    }

    @Test
    void emptyCompositesAreZero() {
        PricingContext ctx = new PricingContext(Grade.VIP, 10_000, true);
        assertEquals(0, CompositeDiscountRule.sumOf().discount(ctx));
        assertEquals(0, CompositeDiscountRule.bestOf().discount(ctx));
    }
}
