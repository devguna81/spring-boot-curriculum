package com.devguna.curriculum.ch25.pricing;

import java.util.List;
import java.util.function.ToLongFunction;

/**
 * 자식 규칙들을 하나의 규칙처럼 다루는 묶음(Composite).
 * 합산(sumOf)과 최대값 선택(bestOf) 두 가지 결합 방식이 있고, 자식으로 또 다른 묶음을 넣을 수 있다.
 */
public class CompositeDiscountRule implements DiscountRule {

    private final List<DiscountRule> children;
    private final boolean sum;

    private CompositeDiscountRule(List<DiscountRule> children, boolean sum) {
        this.children = List.copyOf(children);
        this.sum = sum;
    }

    /** 자식 할인을 모두 더한다. 합계가 주문 금액을 넘으면 주문 금액까지만 깎는다. */
    public static CompositeDiscountRule sumOf(DiscountRule... rules) {
        return new CompositeDiscountRule(List.of(rules), true);
    }

    /** 자식 중 가장 큰 할인 하나만 적용한다(중복 적용 불가 규칙). */
    public static CompositeDiscountRule bestOf(DiscountRule... rules) {
        return new CompositeDiscountRule(List.of(rules), false);
    }

    @Override
    public long discount(PricingContext context) {
        ToLongFunction<DiscountRule> each = rule -> rule.discount(context);
        if (sum) {
            long total = children.stream().mapToLong(each).sum();
            return Math.min(total, context.amount());
        }
        return children.stream().mapToLong(each).max().orElse(0);
    }
}
