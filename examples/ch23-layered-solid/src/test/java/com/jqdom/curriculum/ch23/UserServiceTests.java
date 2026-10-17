package com.jqdom.curriculum.ch23;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jqdom.curriculum.ch23.domain.User;
import com.jqdom.curriculum.ch23.notification.WelcomeNotifier;
import com.jqdom.curriculum.ch23.repository.InMemoryUserRepository;
import com.jqdom.curriculum.ch23.service.DuplicateEmailException;
import com.jqdom.curriculum.ch23.service.UserNotFoundException;
import com.jqdom.curriculum.ch23.service.UserService;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/** 스프링 컨테이너 없이 서비스만 만든다. 협력 객체가 인터페이스라서 가짜로 바꿔 끼울 수 있다. */
class UserServiceTests {

    /** 알림을 보내는 대신 받은 사용자를 기록하는 가짜 구현. */
    static class RecordingNotifier implements WelcomeNotifier {
        final List<User> welcomed = new ArrayList<>();

        @Override
        public void welcome(User user) {
            welcomed.add(user);
        }
    }

    private final RecordingNotifier notifier = new RecordingNotifier();
    private final UserService service = new UserService(new InMemoryUserRepository(), notifier);

    @Test
    void registerSavesUserAndSendsWelcome() {
        User saved = service.register("kim", "kim@example.com");

        assertEquals("kim@example.com", service.get(saved.id()).email());
        assertEquals(List.of(saved), notifier.welcomed);
    }

    @Test
    void duplicateEmailIsRejectedWithoutNotification() {
        service.register("kim", "kim@example.com");

        assertThrows(DuplicateEmailException.class, () -> service.register("other", "KIM@example.com"));
        assertEquals(1, notifier.welcomed.size());
    }

    @Test
    void unknownIdThrowsNotFound() {
        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> service.get(99));
        assertTrue(e.getMessage().contains("99"));
    }
}
