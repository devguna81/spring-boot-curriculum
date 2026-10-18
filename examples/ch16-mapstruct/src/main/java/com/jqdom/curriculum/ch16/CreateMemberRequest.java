package com.jqdom.curriculum.ch16;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 가입 요청. 평문 비밀번호는 요청에만 있고 도메인에는 해시로만 들어간다. */
public record CreateMemberRequest(
        @NotBlank @Size(min = 2, max = 20) String name,
        @NotBlank @Email String email,
        @NotNull @Min(0) @Max(150) Integer age,
        @NotBlank @Size(min = 8, max = 64) String password) {
}
