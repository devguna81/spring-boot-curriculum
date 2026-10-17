package com.devguna.curriculum.ch29;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 생성과 전체 수정에 같이 쓰는 요청 본문. */
public record UserRequest(
        @NotBlank @Size(min = 2, max = 50) String name,
        @NotBlank @Email String email,
        @NotNull @Min(0) @Max(150) Integer age) {
}
