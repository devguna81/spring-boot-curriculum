package com.jqdom.curriculum.ch18.contract;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "오류 응답. code로 분기하고 message는 사람이 읽는 용도다")
public record ErrorBody(
        @Schema(description = "오류 코드", example = "USER_NOT_FOUND") String code,
        @Schema(description = "설명") String message,
        @Schema(description = "필드별 사유. 검증 실패일 때만 채워진다") List<FieldIssue> issues) {
}
