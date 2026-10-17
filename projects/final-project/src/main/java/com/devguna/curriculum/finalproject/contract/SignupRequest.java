package com.devguna.curriculum.finalproject.contract;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank @Size(min = 3, max = 30)
        @Pattern(regexp = "[a-z0-9_]+", message = "영문 소문자, 숫자, 밑줄만 쓸 수 있습니다") String username,
        @NotBlank @Size(min = 8, max = 72) @MaxBytes(72) String password,
        @NotBlank @Size(max = 30) String displayName) {
}
