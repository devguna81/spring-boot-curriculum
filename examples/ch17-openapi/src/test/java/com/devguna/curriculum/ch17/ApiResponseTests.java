package com.devguna.curriculum.ch17;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ApiResponseTests {

    @Test
    void okCarriesDataAndNoError() {
        ApiResponse<String> response = ApiResponse.ok("hello");

        assertTrue(response.success());
        assertEquals("hello", response.data());
        assertNull(response.error());
    }

    @Test
    void failCarriesErrorAndNoData() {
        ApiResponse<Void> response = ApiResponse.fail(ApiError.of(ErrorCode.NOT_FOUND, "none"));

        assertFalse(response.success());
        assertNull(response.data());
        assertEquals("NOT_FOUND", response.error().code());
    }

    @Test
    void everyErrorCodeMapsToAnErrorStatus() {
        for (ErrorCode code : ErrorCode.values()) {
            assertTrue(code.status().isError(), code + " must map to a 4xx or 5xx status");
        }
        assertEquals(HttpStatus.CONFLICT, ErrorCode.DUPLICATE_EMAIL.status());
    }
}
