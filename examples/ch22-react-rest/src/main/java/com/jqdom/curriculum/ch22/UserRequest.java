package com.jqdom.curriculum.ch22;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 등록과 수정에 함께 쓰는 요청 본문. */
public record UserRequest(
        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다")
        String name,

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "이메일 형식이 올바르지 않습니다")
        String email,

        @NotNull(message = "나이는 필수입니다")
        @Min(value = 0, message = "나이는 0 이상이어야 합니다")
        @Max(value = 150, message = "나이는 150 이하여야 합니다")
        Integer age) { }
