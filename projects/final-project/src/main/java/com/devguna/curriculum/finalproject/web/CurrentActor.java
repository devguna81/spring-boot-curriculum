package com.devguna.curriculum.finalproject.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.devguna.curriculum.finalproject.common.Actor;
import com.devguna.curriculum.finalproject.common.UnauthorizedException;
import com.devguna.curriculum.finalproject.security.AppUserDetails;

/**
 * 인증된 사용자를 서비스가 쓰는 Actor로 바꾼다.
 * 서비스는 Spring Security를 모르고, 보안 객체를 아는 곳은 이 클래스뿐이다.
 */
@Component
class CurrentActor {

    Actor require() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails user) {
            return new Actor(user.id(), user.role());
        }
        // 보안 필터가 먼저 막으므로 여기까지 오는 일은 설정 실수일 때뿐이다.
        throw new UnauthorizedException("로그인이 필요합니다");
    }
}
