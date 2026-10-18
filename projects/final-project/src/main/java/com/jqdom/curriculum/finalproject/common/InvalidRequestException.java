package com.jqdom.curriculum.finalproject.common;

import java.util.List;

import com.jqdom.curriculum.finalproject.contract.FieldIssue;

/** 검증 어노테이션으로 잡지 못하는 입력 오류(질의 매개변수 등). */
public class InvalidRequestException extends RuntimeException {

    private final transient List<FieldIssue> issues;

    public InvalidRequestException(String field, String reason) {
        super(field + ": " + reason);
        this.issues = List.of(new FieldIssue(field, reason));
    }

    public List<FieldIssue> issues() {
        return issues;
    }
}
