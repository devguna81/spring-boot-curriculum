package com.devguna.curriculum.ch05;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

/** 메모리 저장소. 요청이 동시에 들어와도 안전하도록 ConcurrentHashMap과 AtomicLong을 쓴다. */
@Repository
public class UserRepository {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public User save(String name, String email, int age) {
        User user = new User(sequence.incrementAndGet(), name, email, age);
        users.put(user.id(), user);
        return user;
    }

    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        List<User> result = new ArrayList<>(users.values());
        result.sort((a, b) -> Long.compare(a.id(), b.id()));
        return result;
    }

    /** 존재하는 id만 교체한다. 없는 id를 넣으면 새로 만들지 않고 빈 값을 돌려준다. */
    public Optional<User> replace(User user) {
        return Optional.ofNullable(users.computeIfPresent(user.id(), (id, old) -> user));
    }

    public boolean deleteById(long id) {
        return users.remove(id) != null;
    }
}
