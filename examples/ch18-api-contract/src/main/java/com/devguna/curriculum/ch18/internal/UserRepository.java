package com.devguna.curriculum.ch18.internal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

@Repository
class UserRepository {

    private final Map<Long, UserEntity> users = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    UserEntity save(UserEntity entity) {
        UserEntity saved = entity.withId(sequence.incrementAndGet());
        users.put(saved.id(), saved);
        return saved;
    }

    Optional<UserEntity> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    List<UserEntity> findAll() {
        return users.values().stream().sorted((a, b) -> Long.compare(a.id(), b.id())).toList();
    }

    boolean existsByEmail(String email) {
        return users.values().stream().anyMatch(user -> user.email().equalsIgnoreCase(email));
    }
}
