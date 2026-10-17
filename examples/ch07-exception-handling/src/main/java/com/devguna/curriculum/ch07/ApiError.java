package com.devguna.curriculum.ch07;

import java.util.List;

/** 오류 정보. details는 필드별 검증 실패처럼 여러 건을 담을 때 쓴다. */
public record ApiError(String code, String message, List<Detail> details) {

    public record Detail(String field, String message) {
    }

    public static ApiError of(ErrorCode code, String message) {
        return new ApiError(code.name(), message, List.of());
    }
}
