package com.devguna.curriculum.ch07;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AllowedEmailDomainValidator implements ConstraintValidator<AllowedEmailDomain, String> {

    private List<String> domains;

    @Override
    public void initialize(AllowedEmailDomain annotation) {
        this.domains = Arrays.asList(annotation.value());
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return true;
        }
        int at = email.lastIndexOf('@');
        return at >= 0 && domains.contains(email.substring(at + 1));
    }
}
