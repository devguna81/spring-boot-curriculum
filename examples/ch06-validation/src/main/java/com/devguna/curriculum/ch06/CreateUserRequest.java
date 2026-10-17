package com.devguna.curriculum.ch06;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 검증 규칙은 요청 DTO의 필드에 선언한다. record에서는 컴포넌트에 붙이면 된다.
 * age가 int가 아니라 Integer인 이유: int는 값을 보내지 않아도 0이 되어 "없음"을 구분할 수 없다.
 */
public record CreateUserRequest(
        @NotBlank @Size(min = 2, max = 20) String name,
        @NotBlank @Email @AllowedEmailDomain({"example.com", "corp.com"}) String email,
        @NotNull @Min(0) @Max(150) Integer age) {
}
