package com.devguna.curriculum.ch16;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/** 부분 수정 요청. 보내지 않은 필드는 null로 들어오고 변경하지 않는다. */
public record PatchMemberRequest(
        @Size(min = 2, max = 20) String name,
        @Email String email,
        @Min(0) @Max(150) Integer age) {
}
