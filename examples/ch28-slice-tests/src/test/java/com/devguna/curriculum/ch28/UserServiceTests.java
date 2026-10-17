package com.devguna.curriculum.ch28;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** 서비스 계층: 스프링 없이 매퍼만 목으로 바꾼다(ch27 방식). */
@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    UserMapper mapper;

    @InjectMocks
    UserService service;

    @Test
    void createReadsTheRowAgainBecauseInsertDoesNotFillCreatedAt() {
        given(mapper.insert(any())).willAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(7L); // MyBatis가 생성된 id를 채워 주는 동작을 흉내 낸다
            return 1;
        });
        User reloaded = new User("kim", "kim@example.com", 28);
        reloaded.setId(7L);
        given(mapper.findById(7)).willReturn(reloaded);

        User created = service.create(new UserRequest("kim", "kim@example.com", 28));

        assertThat(created).isSameAs(reloaded);
    }

    @Test
    void updateIsEmptyWhenNoRowWasChanged() {
        given(mapper.update(any())).willReturn(0);

        Optional<User> result = service.update(99, new UserRequest("kim", "kim@example.com", 28));

        assertThat(result).isEmpty();
        then(mapper).should(never()).findById(99);
    }

    @Test
    void deleteIsTrueOnlyWhenARowWasRemoved() {
        given(mapper.deleteById(1)).willReturn(1);
        given(mapper.deleteById(2)).willReturn(0);

        assertThat(service.delete(1)).isTrue();
        assertThat(service.delete(2)).isFalse();
    }
}
