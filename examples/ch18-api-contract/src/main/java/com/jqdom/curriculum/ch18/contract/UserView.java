package com.jqdom.curriculum.ch18.contract;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 응답")
public record UserView(
        @Schema(description = "식별자", example = "1") long id,
        @Schema(description = "이름", example = "kim") String name,
        @Schema(description = "이메일", example = "kim@example.com") String email,
        @Schema(description = "나이", example = "28") int age,
        @Schema(description = "상태") UserStatus status) {
}
