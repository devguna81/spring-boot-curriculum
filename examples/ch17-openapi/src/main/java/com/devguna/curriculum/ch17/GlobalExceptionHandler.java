package com.devguna.curriculum.ch17;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 모든 컨트롤러에서 던진 예외를 한곳에서 받아 같은 모양(ApiResponse)으로 바꾼다.
 * 가장 구체적인 예외 타입의 핸들러가 먼저 선택되므로, 마지막의 Exception 핸들러는 "나머지 전부"를 맡는다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static ResponseEntity<ApiResponse<Void>> respond(ErrorCode code, String message, List<ApiError.Detail> details) {
        return ResponseEntity.status(code.status())
                .body(ApiResponse.fail(new ApiError(code.name(), message, details)));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        return respond(e.code(), e.getMessage(), List.of());
    }

    /** @Valid @RequestBody 검증 실패. 필드별 메시지를 details에 담는다. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleBodyValidation(MethodArgumentNotValidException e) {
        List<ApiError.Detail> details = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiError.Detail(error.getField(), error.getDefaultMessage()))
                .toList();
        return respond(ErrorCode.VALIDATION_FAILED, "요청 값이 올바르지 않습니다", details);
    }

    /** 경로 변수, 쿼리 파라미터에 붙인 제약 위반. */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleParameterValidation(HandlerMethodValidationException e) {
        List<ApiError.Detail> details = e.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> new ApiError.Detail(
                                result.getMethodParameter().getParameterName(), error.getDefaultMessage())))
                .toList();
        return respond(ErrorCode.VALIDATION_FAILED, "요청 값이 올바르지 않습니다", details);
    }

    /** 깨진 JSON 등 본문을 읽을 수 없는 경우. 파서의 내부 메시지는 노출하지 않는다. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadable(HttpMessageNotReadableException e) {
        return respond(ErrorCode.MALFORMED_REQUEST, "요청 본문을 읽을 수 없습니다", List.of());
    }

    /** /users/abc 처럼 숫자 자리에 문자가 온 경우. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return respond(ErrorCode.MALFORMED_REQUEST, "'" + e.getName() + "' 값의 형식이 올바르지 않습니다", List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return respond(ErrorCode.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다: " + e.getMethod(), List.of());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        return respond(ErrorCode.UNSUPPORTED_MEDIA_TYPE, "Content-Type을 확인하세요", List.of());
    }

    /** 매핑이 없는 경로. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResource(NoResourceFoundException e) {
        return respond(ErrorCode.NOT_FOUND, "존재하지 않는 경로입니다", List.of());
    }

    /**
     * 예상하지 못한 오류. 원인은 서버 로그에만 남기고 응답에는 일반적인 문구만 담는다.
     * 예외 메시지에는 SQL, 경로, 비밀값 같은 내부 정보가 섞일 수 있기 때문이다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e) {
        log.error("Unhandled exception", e);
        return respond(ErrorCode.INTERNAL_ERROR, "서버 내부 오류가 발생했습니다", List.of());
    }
}
