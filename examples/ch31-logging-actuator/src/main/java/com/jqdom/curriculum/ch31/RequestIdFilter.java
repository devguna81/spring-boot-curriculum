package com.jqdom.curriculum.ch31;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 요청마다 번호를 정해 MDC에 넣는다. 이 요청을 처리하는 동안 찍히는 모든 로그에 번호가 붙고,
 * 응답 헤더로도 돌려줘서 클라이언트가 문의할 때 번호를 알려 줄 수 있다.
 * 요청이 끝나면 반드시 지운다. 스레드는 재사용되므로 지우지 않으면 다음 요청에 번호가 남는다.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestIdFilter extends OncePerRequestFilter {

    static final String HEADER = "X-Request-Id";
    static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String incoming = request.getHeader(HEADER);
        String requestId = (incoming == null || incoming.isBlank()) ? UUID.randomUUID().toString().substring(0, 8) : sanitize(incoming);
        MDC.put(MDC_KEY, requestId);
        response.setHeader(HEADER, requestId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

    /** 헤더 값이 그대로 로그와 응답 헤더에 들어가므로 줄바꿈 등을 제거하고 길이를 제한한다. */
    private static String sanitize(String value) {
        String cleaned = value.replaceAll("[^A-Za-z0-9._-]", "");
        return cleaned.isEmpty() ? "invalid" : cleaned.substring(0, Math.min(cleaned.length(), 64));
    }
}
