package com.jqdom.curriculum.finalproject.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import com.jqdom.curriculum.finalproject.contract.ErrorBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 보안 필터에서 걸러진 요청도 컨트롤러가 내는 오류와 같은 모양(ErrorBody)으로 답한다.
 * 필터 단계의 예외는 @RestControllerAdvice에 닿지 않으므로 여기서 직접 쓴다.
 * 401 응답에 WWW-Authenticate 헤더를 싣지 않는다. 싣으면 브라우저가 자체 로그인 창을 띄워 React 화면을 가린다.
 */
@Component
class JsonErrorHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    JsonErrorHandlers(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        write(response, HttpStatus.UNAUTHORIZED, ErrorBody.of("UNAUTHORIZED", "로그인이 필요하거나 아이디 또는 비밀번호가 올바르지 않습니다"));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
            throws IOException {
        write(response, HttpStatus.FORBIDDEN, ErrorBody.of("FORBIDDEN", "권한이 없습니다"));
    }

    private void write(HttpServletResponse response, HttpStatus status, ErrorBody body) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonMapper.writeValueAsString(body));
    }
}
