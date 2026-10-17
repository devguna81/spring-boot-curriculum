package com.jqdom.curriculum.finalproject.contract;

import java.time.LocalDateTime;
import java.util.List;

/** 목록에 쓰는 모양. 본문은 싣지 않는다. */
public record PostSummary(long id, String title, String authorName, List<String> tags, boolean pinned,
                          LocalDateTime createdAt) {
}
