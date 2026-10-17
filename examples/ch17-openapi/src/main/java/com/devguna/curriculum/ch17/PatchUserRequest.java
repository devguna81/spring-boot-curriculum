package com.devguna.curriculum.ch17;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * PATCH는 보낸 필드만 검증한다. {@code @Size}, {@code @Email}, {@code @Min} 등은 null을 통과시키므로
 * {@code @NotNull}을 붙이지 않으면 "보내지 않은 필드"는 검사 대상에서 빠진다.
 * (단, {@code @NotBlank}는 null도 막으므로 여기에는 쓰지 않는다.)
 */
@Schema(description = "사용자 부분 수정 요청. 보낸 필드만 변경한다")
public record PatchUserRequest(
        @Schema(description = "이름", example = "kim2") @Size(min = 2, max = 20) String name,
        @Schema(description = "이메일", example = "kim2@example.com")
        @Email @AllowedEmailDomain({"example.com", "corp.com"}) String email,
        @Schema(description = "나이", example = "29") @Min(0) @Max(150) Integer age) {
}
