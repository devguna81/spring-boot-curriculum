package com.devguna.curriculum.ch25.order;

import com.devguna.curriculum.ch25.pricing.BulkAmountRule;
import com.devguna.curriculum.ch25.pricing.CompositeDiscountRule;
import com.devguna.curriculum.ch25.pricing.DiscountRule;
import com.devguna.curriculum.ch25.pricing.FirstOrderRule;
import com.devguna.curriculum.ch25.pricing.VipRateRule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PricingConfig {

    /** VIP 할인은 항상 더하고, 대량 구매와 첫 주문 할인은 둘 중 큰 것 하나만 적용한다. */
    @Bean
    DiscountRule discountRule() {
        return CompositeDiscountRule.sumOf(
                new VipRateRule(),
                CompositeDiscountRule.bestOf(new BulkAmountRule(), new FirstOrderRule()));
    }
}
