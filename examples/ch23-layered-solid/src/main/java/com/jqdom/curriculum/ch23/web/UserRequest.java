package com.jqdom.curriculum.ch23.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(@NotBlank String name, @NotBlank @Email String email) {
}
