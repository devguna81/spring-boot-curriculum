package com.jqdom.curriculum.ch15;

import java.util.List;

/** JSON 컬럼 하나에 들어가는 구조를 그대로 옮긴 record. 컬럼의 JSON 키와 필드 이름이 같다. */
public record ProductAttributes(String brand, int price, List<String> tags, Specs specs) {

    public record Specs(String color, double weightKg) {
    }
}
