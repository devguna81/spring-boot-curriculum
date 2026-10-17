package com.devguna.curriculum.finalproject.contract;

import java.util.List;

/** 모든 오류 응답의 공통 모양(ch18). issues는 입력 검증 오류일 때만 채운다. */
public record ErrorBody(String code, String message, List<FieldIssue> issues) {

    public static ErrorBody of(String code, String message) {
        return new ErrorBody(code, message, List.of());
    }
}
