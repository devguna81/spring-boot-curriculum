package com.jqdom.curriculum.ch26.member.internal;

import com.jqdom.curriculum.ch26.member.api.MemberApi;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
class JdbcMemberDirectory implements MemberApi {

    private final JdbcClient jdbc;

    JdbcMemberDirectory(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean exists(long memberId) {
        Integer count = jdbc.sql("SELECT COUNT(*) FROM member WHERE id = :id")
                .param("id", memberId).query(Integer.class).single();
        return count > 0;
    }
}
