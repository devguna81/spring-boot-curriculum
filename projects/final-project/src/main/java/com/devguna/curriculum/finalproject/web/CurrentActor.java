package com.devguna.curriculum.finalproject.web;

import org.springframework.stereotype.Component;

import com.devguna.curriculum.finalproject.common.Actor;
import com.devguna.curriculum.finalproject.common.UnauthorizedException;
import com.devguna.curriculum.finalproject.user.api.UserApi;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 요청을 보낸 사용자를 찾는다.
 * f02에서는 X-User-Id 헤더를 임시로 신뢰한다. 누구나 헤더를 위조할 수 있으므로 개발 단계 전용이며,
 * f04에서 Spring Security의 인증 결과로 바꾼다. 컨트롤러는 이 클래스만 알기 때문에 교체 범위가 여기 하나다.
 */
@Component
class CurrentActor {

    static final String HEADER = "X-User-Id";

    private final UserApi userApi;

    CurrentActor(UserApi userApi) {
        this.userApi = userApi;
    }

    Actor require(HttpServletRequest request) {
        String raw = request.getHeader(HEADER);
        if (raw == null || raw.isBlank()) {
            throw new UnauthorizedException("로그인이 필요합니다");
        }
        long id;
        try {
            id = Long.parseLong(raw.trim());
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("사용자 식별값이 올바르지 않습니다");
        }
        return userApi.findById(id)
                .map(user -> new Actor(user.id(), user.role()))
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다"));
    }
}
