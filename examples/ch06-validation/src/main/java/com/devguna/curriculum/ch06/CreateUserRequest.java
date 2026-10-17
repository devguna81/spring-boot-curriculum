package com.devguna.curriculum.ch06;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 검증 규칙은 요청 DTO의 필드에 선언한다. record에서는 컴포넌트에 붙이면 된다.
 * age가 int가 아니라 Integer인 이유: int 필드는 값을 생략하면 검증 단계에 가기 전에 JSON 변환 단계에서 400으로 거절된다.
 * Integer로 두면 생략된 값이 null로 들어와 {@code @NotNull} 위반으로 보고되고, 어느 필드가 문제인지 알 수 있다.
 */
public record CreateUserRequest(
        @NotBlank @Size(min = 2, max = 20) String name,
        @NotBlank @Email @AllowedEmailDomain({"example.com", "corp.com"}) String email,
        @NotNull @Min(0) @Max(150) Integer age) {
}
