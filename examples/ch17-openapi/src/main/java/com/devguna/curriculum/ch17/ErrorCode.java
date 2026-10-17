package com.devguna.curriculum.ch17;

import org.springframework.http.HttpStatus;

/**
 * 오류 종류를 한곳에 모은 목록. 클라이언트는 메시지 문구가 아니라 {@code code} 값으로 분기한다.
 * 문구는 바뀔 수 있지만 코드는 계약이기 때문이다.
 */
public enum ErrorCode {

    VALIDATION_FAILED(HttpStatus.BAD_REQUEST),
    MALFORMED_REQUEST(HttpStatus.BAD_REQUEST),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }
}
