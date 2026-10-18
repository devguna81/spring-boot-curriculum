package com.jqdom.curriculum.ch05;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** 서비스는 Spring 없이 저장소만 만들어 넣어 테스트할 수 있다. */
class UserServiceUnitTests {

    private final UserService service = new UserService(new UserRepository());

    @Test
    void patchChangesOnlyProvidedFields() {
        User created = service.create(new CreateUserRequest("kim", "kim@example.com", 28));

        User patched = service.patch(created.id(), new PatchUserRequest(null, null, 29)).orElseThrow();

        assertEquals("kim", patched.name());
        assertEquals("kim@example.com", patched.email());
        assertEquals(29, patched.age());
    }

    @Test
    void updateDoesNotCreateMissingUser() {
        assertTrue(service.update(999, new UpdateUserRequest("x", "x@example.com", 1)).isEmpty());
        assertTrue(service.list().isEmpty());
    }

    @Test
    void deleteTwiceSecondReturnsFalse() {
        User created = service.create(new CreateUserRequest("kim", "kim@example.com", 28));

        assertTrue(service.delete(created.id()));
        assertFalse(service.delete(created.id()));
    }
}
