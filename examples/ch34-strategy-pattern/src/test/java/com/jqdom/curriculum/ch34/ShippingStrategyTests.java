package com.jqdom.curriculum.ch34;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/** 스프링 없이 전략과 계산기를 직접 조립해 확인한다. */
class ShippingStrategyTests {

    private final ShippingFeeCalculator calculator = new ShippingFeeCalculator(
            List.of(new StandardShipping(), new ExpressShipping(), new PickupShipping()));

    @Test
    void standardIsFreeFromThreshold() {
        assertEquals(3_000, calculator.calculate("standard", new ShippingRequest(49_999, 1)));
        assertEquals(0, calculator.calculate("standard", new ShippingRequest(50_000, 1)));
    }

    @Test
    void expressAddsFeeForWeightOverTwoKg() {
        assertEquals(5_000, calculator.calculate("express", new ShippingRequest(1_000, 2)));
        assertEquals(6_500, calculator.calculate("express", new ShippingRequest(1_000, 5)));
    }

    @Test
    void pickupIsAlwaysFree() {
        assertEquals(0, calculator.calculate("pickup", new ShippingRequest(1_000, 30)));
    }

    @Test
    void newStrategyIsAddedWithoutChangingTheCalculator() {
        ShippingFeeStrategy dawn = new ShippingFeeStrategy() {
            @Override
            public String key() {
                return "dawn";
            }

            @Override
            public long fee(ShippingRequest request) {
                return 7_000;
            }
        };
        ShippingFeeCalculator extended = new ShippingFeeCalculator(
                List.of(new StandardShipping(), new ExpressShipping(), new PickupShipping(), dawn));
        assertEquals(7_000, extended.calculate("dawn", new ShippingRequest(1_000, 1)));
        assertEquals(List.of("dawn", "express", "pickup", "standard"), extended.methods());
    }

    @Test
    void unknownMethodNamesTheAvailableOnes() {
        UnknownShippingMethodException e = assertThrows(UnknownShippingMethodException.class,
                () -> calculator.calculate("drone", new ShippingRequest(1_000, 1)));
        assertEquals("지원하지 않는 배송 방법입니다: drone (사용 가능: [express, pickup, standard])", e.getMessage());
    }

    @Test
    void duplicateKeysFailAtStartup() {
        assertThrows(IllegalStateException.class, () -> new ShippingFeeCalculator(
                List.of(new StandardShipping(), new StandardShipping())));
    }

    @Test
    void negativeValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new ShippingRequest(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> new ShippingRequest(1, -1));
    }
}
