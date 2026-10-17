package com.jqdom.curriculum.ch17;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** PUT은 전체 교체이므로 생성과 같은 규칙을 모든 필드에 적용한다. */
@Schema(description = "사용자 전체 교체 요청. 모든 필드가 필수")
public record UpdateUserRequest(
        @Schema(description = "이름", example = "lee") @NotBlank @Size(min = 2, max = 20) String name,
        @Schema(description = "이메일", example = "lee@example.com")
        @NotBlank @Email @AllowedEmailDomain({"example.com", "corp.com"}) String email,
        @Schema(description = "나이", example = "35") @NotNull @Min(0) @Max(150) Integer age) {
}
