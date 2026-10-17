package com.devguna.curriculum.ch22;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

/** 메모리 저장소를 겸한 서비스. 화면과 통신에 집중하려고 DB는 쓰지 않는다. */
@Service
public class UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public UserService() {
        create(new UserRequest("kim", "kim@example.com", 28));
        create(new UserRequest("lee", "lee@example.com", 35));
        create(new UserRequest("park", "park@example.com", 41));
    }

    public List<User> list() {
        return users.values().stream().sorted(Comparator.comparingLong(User::id)).toList();
    }

    public User get(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    public synchronized User create(UserRequest request) {
        assertEmailAvailable(request.email(), null);
        User user = new User(sequence.incrementAndGet(), request.name(), request.email(), request.age());
        users.put(user.id(), user);
        return user;
    }

    public synchronized User update(long id, UserRequest request) {
        get(id);
        assertEmailAvailable(request.email(), id);
        User user = new User(id, request.name(), request.email(), request.age());
        users.put(id, user);
        return user;
    }

    public void delete(long id) {
        if (users.remove(id) == null) {
            throw new UserNotFoundException(id);
        }
    }

    private void assertEmailAvailable(String email, Long selfId) {
        boolean taken = users.values().stream()
                .anyMatch(u -> u.email().equalsIgnoreCase(email) && (selfId == null || u.id() != selfId));
        if (taken) {
            throw new DuplicateEmailException(email);
        }
    }
}
