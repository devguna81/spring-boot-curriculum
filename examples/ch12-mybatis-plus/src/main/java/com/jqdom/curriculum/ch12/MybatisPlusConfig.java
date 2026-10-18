package com.jqdom.curriculum.ch12;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    /**
     * 페이징 플러그인. 이것이 없으면 selectPage는 LIMIT 없이 전체를 읽고 total도 채우지 않는다.
     * DbType은 SQL 방언을 정한다. 이 예제는 H2 기준이고, Oracle로 바꿀 때는 DbType.ORACLE을 쓴다.
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
        return interceptor;
    }
}
