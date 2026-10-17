package com.jqdom.curriculum.finalproject.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jqdom.curriculum.finalproject.user.api.UserApi;

/** 로그인 요청의 아이디로 DB의 계정을 찾아 Spring Security에 넘긴다. 비밀번호 비교는 Spring Security가 한다. */
@Service
class AppUserDetailsService implements UserDetailsService {

    private final UserApi userApi;

    AppUserDetailsService(UserApi userApi) {
        this.userApi = userApi;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userApi.findAccount(username)
                .map(account -> new AppUserDetails(account.id(), account.username(), account.passwordHash(), account.role()))
                .orElseThrow(() -> new UsernameNotFoundException("계정을 찾을 수 없습니다"));
    }
}
