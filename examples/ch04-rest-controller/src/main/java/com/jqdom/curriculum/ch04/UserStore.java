package com.jqdom.curriculum.ch04;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

/**
 * DB 없이 메모리에 사용자 목록을 들고 있는 저장소. 데이터베이스는 3부(ch08 이후)에서 붙인다.
 * 이 챕터는 조회만 다루므로 목록은 시작할 때 고정된다.
 */
@Component
public class UserStore {

    private final List<User> users = List.of(
            new User(1, "kim", "kim@example.com", 28),
            new User(2, "lee", "lee@example.com", 35),
            new User(3, "park", "park@example.com", 42),
            new User(4, "kimberly", "kimberly@example.com", 31));

    public List<User> findAll() {
        return users;
    }

    public Optional<User> findById(long id) {
        return users.stream().filter(user -> user.id() == id).findFirst();
    }
}
