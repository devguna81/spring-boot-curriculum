package com.jqdom.curriculum.ch34;

import java.util.Set;

public class UnknownShippingMethodException extends RuntimeException {

    public UnknownShippingMethodException(String method, Set<String> available) {
        super("지원하지 않는 배송 방법입니다: " + method + " (사용 가능: " + available + ")");
    }
}
