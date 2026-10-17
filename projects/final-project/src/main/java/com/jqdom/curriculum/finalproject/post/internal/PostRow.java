package com.jqdom.curriculum.finalproject.post.internal;

import java.time.LocalDateTime;

/** 목록 검색 결과의 한 줄. 본문(content)은 목록에 필요 없어 읽지 않는다. */
record PostRow(long id, String title, String authorName, PostMeta meta, boolean pinned, LocalDateTime createdAt) {
}
