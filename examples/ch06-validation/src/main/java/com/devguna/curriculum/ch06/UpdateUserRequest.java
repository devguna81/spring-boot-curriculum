package com.devguna.curriculum.ch06;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** PUT은 전체 교체이므로 생성과 같은 규칙을 모든 필드에 적용한다. */
public record UpdateUserRequest(
        @NotBlank @Size(min = 2, max = 20) String name,
        @NotBlank @Email @AllowedEmailDomain({"example.com", "corp.com"}) String email,
        @NotNull @Min(0) @Max(150) Integer age) {
}
