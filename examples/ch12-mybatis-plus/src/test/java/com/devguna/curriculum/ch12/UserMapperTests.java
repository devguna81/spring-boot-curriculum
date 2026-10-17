package com.devguna.curriculum.ch12;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

/** ch09의 UserMapperTests와 같은 시나리오를 BaseMapper로 다시 통과시킨다. */
@SpringBootTest
@Transactional
class UserMapperTests {

    @Autowired
    UserMapper mapper;

    private User insertUser(String name, String email, int age) {
        User user = new User(name, email, age);
        mapper.insert(user);
        return user;
    }

    @Test
    void initialDataIsLoaded() {
        List<User> users = mapper.selectList(new LambdaQueryWrapper<User>().orderByAsc(User::getId));

        assertEquals(List.of("kim", "lee", "park"), users.stream().map(User::getName).toList());
    }

    @Test
    void selectByIdFillsEveryFieldIncludingCreatedAt() {
        User kim = mapper.selectById(1);

        assertEquals("kim@example.com", kim.getEmail());
        assertEquals(28, kim.getAge());
        assertNotNull(kim.getCreatedAt());
    }

    @Test
    void insertFillsGeneratedId() {
        User user = insertUser("choi", "choi@example.com", 30);

        assertNotNull(user.getId());
        assertEquals("choi", mapper.selectById(user.getId()).getName());
    }

    @Test
    void selectByIdReturnsNullWhenMissing() {
        assertNull(mapper.selectById(999));
    }

    @Test
    void updateByIdReportsAffectedRows() {
        User user = new User("kim2", "kim2@example.com", 29);
        user.setId(1L);

        assertEquals(1, mapper.updateById(user));
        assertEquals("kim2", mapper.selectById(1).getName());

        user.setId(999L);
        assertEquals(0, mapper.updateById(user));
    }

    @Test
    void updateByIdSkipsNullFields() {
        User partial = new User();
        partial.setId(1L);
        partial.setAge(50);

        mapper.updateById(partial);

        User kim = mapper.selectById(1);
        assertEquals(50, kim.getAge());
        assertEquals("kim", kim.getName());
        assertEquals("kim@example.com", kim.getEmail());
    }

    @Test
    void deleteByIdReportsAffectedRows() {
        assertEquals(1, mapper.deleteById(3));
        assertEquals(0, mapper.deleteById(3));
        assertEquals(2, mapper.selectCount(null));
    }

    @Test
    void uniqueViolationIsTranslatedToSpringException() {
        assertThrows(DuplicateKeyException.class, () -> insertUser("other", "kim@example.com", 20));
    }

    @Test
    void lambdaWrapperBuildsConditions() {
        List<User> found = mapper.selectList(new LambdaQueryWrapper<User>()
                .ge(User::getAge, 30)
                .likeRight(User::getEmail, "p")
                .orderByAsc(User::getId));

        assertEquals(List.of("park"), found.stream().map(User::getName).toList());
    }

    @Test
    void falseConditionLeavesOutThePredicate() {
        String nameFilter = null;

        long count = mapper.selectCount(new LambdaQueryWrapper<User>()
                .like(nameFilter != null, User::getName, nameFilter));

        assertEquals(3, count);
    }

    @Test
    void paginationInterceptorAddsLimitAndTotal() {
        for (int i = 1; i <= 22; i++) {
            insertUser("bulk" + i, "bulk" + i + "@example.com", 20 + i);
        }

        Page<User> page = mapper.selectPage(new Page<>(2, 10), new LambdaQueryWrapper<User>().orderByAsc(User::getId));

        assertEquals(10, page.getRecords().size());
        assertEquals(25, page.getTotal());
        assertEquals(3, page.getPages());
        assertEquals("bulk8", page.getRecords().get(0).getName());
    }

    @Test
    void xmlMapperCoexistsWithBaseMapper() {
        List<User> older = mapper.findOlderThan(30);

        assertEquals(List.of("park", "lee"), older.stream().map(User::getName).toList());
    }
}
