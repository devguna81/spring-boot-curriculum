package com.devguna.curriculum.ch17;

/**
 * 모든 응답의 공통 껍데기. 성공이면 data, 실패면 error가 채워지고 다른 쪽은 null이다.
 * 클라이언트는 응답이 무엇이든 {@code success}부터 확인하면 된다.
 */
public record ApiResponse<T>(boolean success, T data, ApiError error) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> fail(ApiError error) {
        return new ApiResponse<>(false, null, error);
    }
}
