package com.jqdom.curriculum.ch36.orchestration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 호출 흐름 전체가 공유하는 공통 맥락. Map이나 JsonNode가 아니라 명시적 타입을 쓴다.
 * 가변 IN 값(회원 id 목록)도 List&lt;Long&gt; 타입으로 들고 다니며, 만들 때 중복을 제거하고 불변으로 고정한다.
 */
public record CallContext(List<Long> memberIds) {

    public CallContext {
        if (memberIds == null) {
            throw new IllegalArgumentException("회원 id 목록이 없습니다.");
        }
        memberIds = List.copyOf(new ArrayList<>(new LinkedHashSet<>(memberIds)));
    }
}
