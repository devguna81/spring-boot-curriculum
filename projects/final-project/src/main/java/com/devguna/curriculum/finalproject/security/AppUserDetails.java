package com.devguna.curriculum.finalproject.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.devguna.curriculum.finalproject.contract.Role;

/** 인증된 사용자. 컨트롤러가 사용자 ID와 역할을 꺼내 쓸 수 있도록 Spring Security 기본 User 대신 직접 만든다. */
public record AppUserDetails(long id, String username, String passwordHash, Role role) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /** 로그에 비밀번호 해시가 찍히지 않도록 직접 정의한다. */
    @Override
    public String toString() {
        return "AppUserDetails[id=" + id + ", username=" + username + ", role=" + role + "]";
    }
}
