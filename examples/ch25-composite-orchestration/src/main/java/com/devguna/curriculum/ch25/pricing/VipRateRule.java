package com.devguna.curriculum.ch25.pricing;

/** VIP 등급이면 금액의 10%를 깎는다. */
public class VipRateRule implements DiscountRule {

    @Override
    public long discount(PricingContext context) {
        return context.grade() == Grade.VIP ? context.amount() / 10 : 0;
    }
}
