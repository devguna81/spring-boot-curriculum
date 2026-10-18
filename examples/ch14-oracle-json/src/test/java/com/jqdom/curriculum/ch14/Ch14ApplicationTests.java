package com.jqdom.curriculum.ch14;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** 기본 Profile(H2)에서 항상 실행된다. JSON 질의 자체는 실행하지 않고, Mapper XML이 문법 오류 없이 로드되는지만 본다. */
@SpringBootTest
class Ch14ApplicationTests {

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Test
    void contextLoadsAndJsonStatementsAreRegistered() {
        var statements = sqlSessionFactory.getConfiguration().getMappedStatementNames();

        assertTrue(statements.contains("com.jqdom.curriculum.ch14.ProductMapper.findTags"));
        assertTrue(statements.contains("com.jqdom.curriculum.ch14.ProductMapper.findByTag"));
    }
}
