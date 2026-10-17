package com.jqdom.curriculum.finalproject.contract;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 글 등록과 수정에 같이 쓴다. tags는 생략하면 빈 목록, pinned는 생략하면 false로 본다. */
public record PostRequest(
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 5000) String content,
        @Size(max = 5) List<@NotBlank @Size(max = 20) String> tags,
        Boolean pinned) {
}
