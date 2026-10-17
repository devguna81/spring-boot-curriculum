package com.devguna.curriculum.ch27;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * 저장소와 메일 발송을 Mockito 목으로 바꾸고 SignupService의 규칙만 검사한다.
 * MockitoExtension은 엄격 모드라서, 선언만 하고 쓰지 않은 stub이 있으면 테스트가 실패한다.
 */
@ExtendWith(MockitoExtension.class)
class SignupServiceTests {

    private static final Clock TODAY = Clock.fixed(Instant.parse("2026-10-17T00:00:00Z"), ZoneOffset.UTC);

    @Mock
    UserRepository repository;

    @Mock
    MailSender mailSender;

    @Captor
    ArgumentCaptor<String> emailCaptor;

    SignupService service;

    @BeforeEach
    void setUp() {
        service = new SignupService(repository, mailSender, TODAY);
    }

    @Nested
    @DisplayName("가입에 성공하는 경우")
    class Success {

        @Test
        void savesLowerCasedEmailWithTodayAndSendsWelcome() {
            given(repository.existsByEmail("kim@example.com")).willReturn(false);
            given(repository.save("kim", "kim@example.com", LocalDate.parse("2026-10-17")))
                    .willReturn(new User(1, "kim", "kim@example.com", LocalDate.parse("2026-10-17")));

            User saved = service.signup("kim", "  KIM@Example.com ");

            assertThat(saved.id()).isEqualTo(1);
            then(mailSender).should().sendWelcome("kim@example.com", "kim");
        }

        @Test
        void mailIsSentOnlyAfterSave() {
            given(repository.save(anyString(), anyString(), any()))
                    .willReturn(new User(1, "kim", "kim@example.com", LocalDate.parse("2026-10-17")));

            service.signup("kim", "kim@example.com");

            InOrder order = inOrder(repository, mailSender);
            order.verify(repository).save(anyString(), anyString(), any());
            order.verify(mailSender).sendWelcome("kim@example.com", "kim");
        }

        @Test
        void capturesWhatWasPassedToTheMailSender() {
            given(repository.save(anyString(), anyString(), any()))
                    .willReturn(new User(7, "lee", "lee@example.com", LocalDate.parse("2026-10-17")));

            service.signup("lee", "LEE@example.com");

            then(mailSender).should().sendWelcome(emailCaptor.capture(), anyString());
            assertThat(emailCaptor.getValue()).isEqualTo("lee@example.com");
        }
    }

    @Nested
    @DisplayName("가입을 거절하는 경우")
    class Rejection {

        @Test
        void duplicateEmailIsRejectedAndNothingIsSavedOrSent() {
            given(repository.existsByEmail("kim@example.com")).willReturn(true);

            assertThatThrownBy(() -> service.signup("kim", "KIM@example.com"))
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasMessageContaining("kim@example.com");

            then(repository).should(never()).save(anyString(), anyString(), any());
            then(mailSender).shouldHaveNoInteractions();
        }

        @Test
        void blankNameIsRejectedBeforeAnyCollaboratorIsCalled() {
            assertThatThrownBy(() -> service.signup(" ", "kim@example.com"))
                    .isInstanceOf(IllegalArgumentException.class);

            then(repository).shouldHaveNoInteractions();
            then(mailSender).shouldHaveNoInteractions();
        }

        @Test
        void mailFailurePropagatesAfterTheUserWasSaved() {
            given(repository.save(anyString(), anyString(), any()))
                    .willReturn(new User(1, "kim", "kim@example.com", LocalDate.parse("2026-10-17")));
            org.mockito.Mockito.doThrow(new IllegalStateException("smtp down"))
                    .when(mailSender).sendWelcome(anyString(), anyString());

            assertThatThrownBy(() -> service.signup("kim", "kim@example.com"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("smtp down");

            // 저장은 이미 끝났다. 이 서비스는 메일 실패를 되돌리지 않는다는 사실을 테스트로 드러낸다.
            then(repository).should().save("kim", "kim@example.com", LocalDate.parse("2026-10-17"));
        }
    }
}
