package com.jqdom.curriculum.ch24.adapter.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MemberRequest(@NotBlank String name,
                            @Pattern(regexp = "\\d{5}", message = "우편번호는 숫자 5자리여야 합니다") String postalCode) {
}
