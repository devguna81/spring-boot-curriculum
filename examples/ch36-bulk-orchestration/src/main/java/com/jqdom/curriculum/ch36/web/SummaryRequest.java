package com.jqdom.curriculum.ch36.web;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** 가변 길이 id 목록. 비어 있거나, 1000개를 넘거나, 양수가 아닌 값이 있으면 컨트롤러 입구에서 400으로 거절한다. */
public record SummaryRequest(
        @NotEmpty(message = "memberIds는 비어 있을 수 없습니다.")
        @Size(max = 1000, message = "memberIds는 최대 1000개까지 보낼 수 있습니다.")
        List<@NotNull(message = "memberIds에 null이 있습니다.") @Positive(message = "memberIds는 양수여야 합니다.") Long> memberIds) {
}
