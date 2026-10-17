package com.devguna.curriculum.finalproject.contract;

import java.time.LocalDateTime;
import java.util.List;

public record PostView(long id, String title, String content, long authorId, String authorName,
                       List<String> tags, boolean pinned, LocalDateTime createdAt) {
}
