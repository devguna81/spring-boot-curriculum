package com.jqdom.curriculum.ch25.web;

import com.jqdom.curriculum.ch25.pricing.Grade;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(@NotBlank String customerId, @NotNull Grade grade, @NotBlank String sku,
                           @Min(1) int quantity, @Min(1) long unitPrice, boolean firstOrder) {
}
