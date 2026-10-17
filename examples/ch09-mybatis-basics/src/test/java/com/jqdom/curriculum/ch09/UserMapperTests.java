package com.jqdom.curriculum.ch09;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

/** 각 테스트는 트랜잭션 안에서 실행되고 끝나면 롤백되어 다른 테스트에 영향을 주지 않는다. */
@SpringBootTest
@Transactional
class UserMapperTests {

    @Autowired
    UserMapper mapper;

    @Test
    void initialDataIsLoaded() {
        List<User> users = mapper.findAll();

        assertEquals(List.of("kim", "lee", "park"), users.stream().map(User::getName).toList());
    }

    @Test
    void resultMapFillsEveryFieldIncludingCreatedAt() {
        User kim = mapper.findById(1);

        assertEquals("kim@example.com", kim.getEmail());
        assertEquals(28, kim.getAge());
        assertNotNull(kim.getCreatedAt());
    }

    @Test
    void insertFillsGeneratedId() {
        User user = new User("choi", "choi@example.com", 30);

        int affected = mapper.insert(user);

        assertEquals(1, affected);
        assertNotNull(user.getId());
        assertEquals("choi", mapper.findById(user.getId()).getName());
    }

    @Test
    void findByIdReturnsNullWhenMissing() {
        assertNull(mapper.findById(999));
    }

    @Test
    void updateReportsAffectedRows() {
        User user = new User("kim2", "kim2@example.com", 29);
        user.setId(1L);

        assertEquals(1, mapper.update(user));
        assertEquals("kim2", mapper.findById(1).getName());

        user.setId(999L);
        assertEquals(0, mapper.update(user));
    }

    @Test
    void deleteReportsAffectedRows() {
        assertEquals(1, mapper.deleteById(3));
        assertEquals(0, mapper.deleteById(3));
        assertEquals(2, mapper.count());
    }

    @Test
    void uniqueViolationIsTranslatedToSpringException() {
        User duplicate = new User("other", "kim@example.com", 20);

        assertThrows(DuplicateKeyException.class, () -> mapper.insert(duplicate));
    }

    @Test
    void hashParameterIsBoundAsValueNotSql() {
        User hostile = new User("x'); DROP TABLE users; --", "hostile@example.com", 20);
        mapper.insert(hostile);

        assertEquals("x'); DROP TABLE users; --", mapper.findById(hostile.getId()).getName());
        assertEquals(4, mapper.count());
    }
}
