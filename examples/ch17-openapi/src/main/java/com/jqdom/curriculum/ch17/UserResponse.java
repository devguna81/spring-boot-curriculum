package com.jqdom.curriculum.ch17;

import io.swagger.v3.oas.annotations.media.Schema;

/** 응답 본문. 내부 객체를 그대로 노출하지 않고 응답용 형태로 한 번 감싼다. */
@Schema(description = "사용자 응답")
public record UserResponse(
        @Schema(description = "식별자", example = "1") long id,
        @Schema(description = "이름", example = "kim") String name,
        @Schema(description = "이메일", example = "kim@example.com") String email,
        @Schema(description = "나이", example = "28") int age) {

    public static UserResponse from(User user) {
        return new UserResponse(user.id(), user.name(), user.email(), user.age());
    }
}
