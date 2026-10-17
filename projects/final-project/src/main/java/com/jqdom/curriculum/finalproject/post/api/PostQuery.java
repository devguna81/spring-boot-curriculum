package com.jqdom.curriculum.finalproject.post.api;

/** 목록 검색 조건. 값이 null인 항목은 조건에서 빠진다. */
public record PostQuery(String keyword, Long authorId, String tag, int page, int size) {

    public static final int MAX_SIZE = 50;
}
