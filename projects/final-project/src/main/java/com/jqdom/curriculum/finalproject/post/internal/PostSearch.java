package com.jqdom.curriculum.finalproject.post.internal;

/** XML 매퍼에 넘기는 검색 조건. keywordPattern과 tagPattern은 LIKE용으로 이미 이스케이프된 값이다. */
record PostSearch(String keywordPattern, Long authorId, String tag, String tagPattern, long offset, int size) {
}
