package com.jqdom.curriculum.finalproject;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import com.jqdom.curriculum.finalproject.contract.PostRequest;
import com.jqdom.curriculum.finalproject.contract.SignupRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;

/** 계약에 적은 검증 규칙이 실제로 동작하는지 확인한다. */
class ContractValidationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static Set<String> fieldsOf(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream().map(v -> v.getPropertyPath().toString()).collect(java.util.stream.Collectors.toSet());
    }

    @Test
    void validSignupHasNoViolations() {
        assertThat(validator.validate(new SignupRequest("kim_01", "password1", "Kim"))).isEmpty();
    }

    @Test
    void signupRejectsBadUsernameShortPasswordAndBlankName() {
        var violations = validator.validate(new SignupRequest("Kim!", "short", " "));

        assertThat(fieldsOf(violations)).containsExactlyInAnyOrder("username", "password", "displayName");
    }

    @Test
    void passwordIsLimitedByUtf8BytesNotByCharacters() {
        // 72바이트: 한글 24자는 통과, 25자(75바이트)는 글자 수가 72 이하여도 거부
        assertThat(validator.validate(new SignupRequest("kim_01", "가".repeat(24), "Kim"))).isEmpty();
        var violations = validator.validate(new SignupRequest("kim_01", "가".repeat(25), "Kim"));

        assertThat(fieldsOf(violations)).containsExactly("password");
        assertThat(validator.validate(new SignupRequest("kim_01", "a".repeat(72), "Kim"))).isEmpty();
        assertThat(validator.validate(new SignupRequest("kim_01", "a".repeat(73), "Kim"))).isNotEmpty();
    }

    @Test
    void postRequestLimitsTagCountAndTagLength() {
        var tooMany = new PostRequest("t", "c", List.of("a", "b", "c", "d", "e", "f"), null);
        var longTag = new PostRequest("t", "c", List.of("x".repeat(21)), null);

        assertThat(fieldsOf(validator.validate(tooMany))).containsExactly("tags");
        assertThat(fieldsOf(validator.validate(longTag))).hasSize(1);
    }

    @Test
    void postRequestAllowsOmittedTagsAndPinned() {
        assertThat(validator.validate(new PostRequest("title", "content", null, null))).isEmpty();
    }
}
