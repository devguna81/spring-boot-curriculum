package com.devguna.curriculum.finalproject.contract;

import java.util.List;

/** page는 0부터 시작한다. */
public record PageResponse<T>(List<T> items, int page, int size, long total) {
}
