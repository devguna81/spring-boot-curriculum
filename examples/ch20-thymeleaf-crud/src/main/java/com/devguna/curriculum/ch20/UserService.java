package com.devguna.curriculum.ch20;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

/** 메모리 저장소를 겸한 서비스. 화면 흐름에 집중하려고 DB 없이 구현한다. */
@Service
public class UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public List<User> list() {
        return users.values().stream().sorted(java.util.Comparator.comparingLong(User::id)).toList();
    }

    public User get(long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    public synchronized User create(UserForm form) {
        assertEmailAvailable(form.getEmail(), null);
        User user = new User(sequence.incrementAndGet(), form.getName(), form.getEmail(), form.getAge());
        users.put(user.id(), user);
        return user;
    }

    public synchronized User update(long id, UserForm form) {
        get(id);
        assertEmailAvailable(form.getEmail(), id);
        User user = new User(id, form.getName(), form.getEmail(), form.getAge());
        users.put(id, user);
        return user;
    }

    public void delete(long id) {
        if (users.remove(id) == null) {
            throw new UserNotFoundException(id);
        }
    }

    /** 수정할 때는 자기 자신의 이메일을 그대로 두는 것을 허용하려고 selfId 를 제외한다. */
    private void assertEmailAvailable(String email, Long selfId) {
        boolean taken = users.values().stream()
                .anyMatch(u -> u.email().equalsIgnoreCase(email) && (selfId == null || u.id() != selfId));
        if (taken) {
            throw new DuplicateEmailException(email);
        }
    }
}
