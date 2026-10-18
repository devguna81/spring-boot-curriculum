package com.jqdom.curriculum.ch34;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * 전략을 고르고 호출만 한다. 배송 방법별 if/else가 없으므로 방법이 늘어도 이 클래스는 바뀌지 않는다.
 * 같은 key를 가진 전략이 둘이면 애플리케이션이 기동하지 않는다(toUnmodifiableMap이 예외를 던진다).
 */
@Service
public class ShippingFeeCalculator {

    private final Map<String, ShippingFeeStrategy> strategies;

    public ShippingFeeCalculator(List<ShippingFeeStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toUnmodifiableMap(ShippingFeeStrategy::key, Function.identity()));
    }

    public long calculate(String method, ShippingRequest request) {
        ShippingFeeStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new UnknownShippingMethodException(method, new TreeSet<>(strategies.keySet()));
        }
        return strategy.fee(request);
    }

    public List<String> methods() {
        return strategies.keySet().stream().sorted().toList();
    }
}
