package com.jqdom.curriculum.ch06;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;

/**
 * 스프링 없이 Jakarta Validation만으로 제약을 검사한다. 제약은 컨트롤러가 아니라 DTO에 붙어 있기 때문에 가능하다.
 * 메시지 문구는 로케일에 따라 달라지므로 위반된 필드와 제약 종류만 비교한다.
 */
class ConstraintTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static Set<String> violated(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(v -> v.getPropertyPath() + ":" + v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName())
                .collect(Collectors.toSet());
    }

    @Test
    void validRequestHasNoViolations() {
        assertTrue(validator.validate(new CreateUserRequest("kim", "kim@example.com", 28)).isEmpty());
    }

    @Test
    void everyRuleReportsItsOwnViolation() {
        Set<String> result = violated(validator.validate(new CreateUserRequest(" ", "not-an-email", 200)));

        assertTrue(result.contains("name:NotBlank"));
        assertTrue(result.contains("email:Email"));
        assertTrue(result.contains("age:Max"));
    }

    @Test
    void missingValuesAreRejectedOnCreate() {
        Set<String> result = violated(validator.validate(new CreateUserRequest(null, null, null)));

        assertEquals(Set.of("name:NotBlank", "email:NotBlank", "age:NotNull"), result);
    }

    @Test
    void customConstraintRejectsOtherDomains() {
        Set<String> result = violated(validator.validate(new CreateUserRequest("kim", "kim@gmail.com", 28)));

        assertEquals(Set.of("email:AllowedEmailDomain"), result);
    }

    @Test
    void customConstraintAcceptsListedDomains() {
        assertTrue(validator.validate(new CreateUserRequest("kim", "kim@corp.com", 28)).isEmpty());
    }

    @Test
    void patchSkipsFieldsThatWereNotSent() {
        assertTrue(validator.validate(new PatchUserRequest(null, null, null)).isEmpty());
        assertEquals(Set.of("age:Min"), violated(validator.validate(new PatchUserRequest(null, null, -1))));
    }

    @Test
    void customValidatorTreatsNullAsValid() {
        assertTrue(new AllowedEmailDomainValidator().isValid(null, null));
    }
}
