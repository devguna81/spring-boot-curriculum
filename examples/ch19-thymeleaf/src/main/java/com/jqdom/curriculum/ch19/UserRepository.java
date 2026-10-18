package com.jqdom.curriculum.ch19;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

/** 메모리에 고정 데이터를 가진 저장소. 화면 학습에 집중하려고 DB는 쓰지 않는다. */
@Repository
public class UserRepository {

    private final List<User> users = List.of(
            new User(1, "kim", "kim@example.com", 28, true),
            new User(2, "lee", "lee@example.com", 35, true),
            new User(3, "park", "park@example.com", 41, false),
            // HTML 이스케이프를 확인하기 위한 데이터. 이름에 태그가 들어 있다.
            new User(4, "<b>choi</b>", "choi@example.com", 23, true));

    public List<User> findAll() {
        return users;
    }

    public Optional<User> findById(long id) {
        return users.stream().filter(user -> user.id() == id).findFirst();
    }
}
