package com.devguna.curriculum.ch18.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "사용자 생성 요청")
public record CreateUserRequest(
        @Schema(description = "이름", example = "kim") @NotBlank @Size(min = 2, max = 20) String name,
        @Schema(description = "이메일", example = "kim@example.com") @NotBlank @Email String email,
        @Schema(description = "나이", example = "28") @NotNull @Min(0) @Max(150) Integer age) {
}
