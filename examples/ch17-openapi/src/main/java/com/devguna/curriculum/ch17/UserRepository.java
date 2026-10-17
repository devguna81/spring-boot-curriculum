package com.devguna.curriculum.ch17;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

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

    public Optional<User> findByEmail(String email) {
        return users.values().stream().filter(user -> user.email().equalsIgnoreCase(email)).findFirst();
    }

    public List<User> findAll() {
        List<User> result = new ArrayList<>(users.values());
        result.sort((a, b) -> Long.compare(a.id(), b.id()));
        return result;
    }

    public Optional<User> replace(User user) {
        return Optional.ofNullable(users.computeIfPresent(user.id(), (id, old) -> user));
    }

    public boolean deleteById(long id) {
        return users.remove(id) != null;
    }
}
