package com.jqdom.curriculum.ch23.service;

import com.jqdom.curriculum.ch23.domain.User;
import com.jqdom.curriculum.ch23.notification.WelcomeNotifier;
import com.jqdom.curriculum.ch23.repository.UserRepository;

import org.springframework.stereotype.Service;

/**
 * 업무 규칙(이메일 중복 금지, 가입 후 알림)만 가진다.
 * HTTP도 저장 방식도 모르고, 두 협력 객체를 인터페이스로만 받는다.
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final WelcomeNotifier notifier;

    public UserService(UserRepository repository, WelcomeNotifier notifier) {
        this.repository = repository;
        this.notifier = notifier;
    }

    public User register(String name, String email) {
        if (repository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
        User saved = repository.save(name, email);
        notifier.welcome(saved);
        return saved;
    }

    public User get(long id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
