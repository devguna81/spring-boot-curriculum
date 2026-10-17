package com.devguna.curriculum.ch12;

import java.util.List;

public record PageResponse<T>(List<T> items, long page, long size, long totalElements, long totalPages) {
}
