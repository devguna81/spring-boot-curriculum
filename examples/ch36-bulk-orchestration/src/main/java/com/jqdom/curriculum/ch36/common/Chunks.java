package com.jqdom.curriculum.ch36.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

/**
 * 가변 길이 값 목록을 IN 절에 안전하게 넘기기 위한 도구.
 * 중복을 제거하고, 순서를 유지한 채 일정 크기로 나눈다.
 * DB는 IN 목록의 길이를 제한하거나 길수록 느려지므로 한 번에 넘기는 개수를 설정으로 정한다.
 */
public final class Chunks {

    private Chunks() {
    }

    public static List<List<Long>> partition(Collection<Long> values, int size) {
        if (values == null) {
            throw new IllegalArgumentException("값 목록이 없습니다.");
        }
        if (size < 1) {
            throw new IllegalArgumentException("묶음 크기는 1 이상이어야 합니다: " + size);
        }
        List<Long> distinct = new ArrayList<>(new LinkedHashSet<>(values));
        List<List<Long>> chunks = new ArrayList<>();
        for (int from = 0; from < distinct.size(); from += size) {
            chunks.add(List.copyOf(distinct.subList(from, Math.min(from + size, distinct.size()))));
        }
        return chunks;
    }

    /** 값 목록을 묶음으로 나누어 조회 함수를 묶음마다 한 번씩 부르고, 결과를 이어 붙인다. 빈 목록이면 함수를 부르지 않는다. */
    public static <R> List<R> lookup(Collection<Long> values, int size, Function<List<Long>, List<R>> query) {
        List<R> result = new ArrayList<>();
        for (List<Long> chunk : partition(values, size)) {
            result.addAll(query.apply(chunk));
        }
        return result;
    }
}
