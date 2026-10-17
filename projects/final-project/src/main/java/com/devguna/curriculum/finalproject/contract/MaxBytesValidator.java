package com.devguna.curriculum.finalproject.contract;

import java.nio.charset.StandardCharsets;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxBytesValidator implements ConstraintValidator<MaxBytes, CharSequence> {

    private int max;

    @Override
    public void initialize(MaxBytes annotation) {
        this.max = annotation.value();
    }

    /** null은 통과시킨다. 필수 여부는 @NotBlank가 따로 판단한다. */
    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        return value == null || value.toString().getBytes(StandardCharsets.UTF_8).length <= max;
    }
}
