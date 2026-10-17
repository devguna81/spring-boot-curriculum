package com.devguna.curriculum.ch22;

import java.util.List;

/** 모든 오류 응답의 형식. 화면은 code 로 분기하고, 필드 오류는 issues 로 입력 칸 옆에 보여 준다. */
public record ErrorBody(String code, String message, List<FieldIssue> issues) {

    public record FieldIssue(String field, String reason) { }
}
