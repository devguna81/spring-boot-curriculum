package com.devguna.curriculum.finalproject.post.internal;

import java.util.List;

/** meta 컬럼에 JSON으로 저장되는 값. 지금은 태그뿐이지만 컬럼을 바꾸지 않고 항목을 늘릴 수 있다. */
public record PostMeta(List<String> tags) {

    public PostMeta {
        tags = tags == null ? List.of() : List.copyOf(tags);
    }
}
