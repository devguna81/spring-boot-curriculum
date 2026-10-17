package com.devguna.curriculum.ch27;

import java.time.Clock;
import java.time.LocalDate;

/**
 * 가입 규칙: 이메일은 소문자로 저장하고, 중복이면 거절하고, 저장이 끝난 뒤에만 환영 메일을 보낸다.
 * 현재 날짜는 Clock으로 받아서 테스트가 시간을 고정할 수 있다.
 */
public class SignupService {

    private final UserRepository repository;
    private final MailSender mailSender;
    private final Clock clock;

    public SignupService(UserRepository repository, MailSender mailSender, Clock clock) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.clock = clock;
    }

    public User signup(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 비어 있을 수 없습니다");
        }
        String normalized = email.trim().toLowerCase();
        if (repository.existsByEmail(normalized)) {
            throw new DuplicateEmailException(normalized);
        }
        User saved = repository.save(name, normalized, LocalDate.now(clock));
        mailSender.sendWelcome(saved.email(), saved.name());
        return saved;
    }
}
