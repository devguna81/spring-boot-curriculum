package com.jqdom.curriculum.ch36;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.jqdom.curriculum.ch36.common.Chunks;

import org.junit.jupiter.api.Test;

class ChunksTests {

    @Test
    void splitsAtTheBoundary() {
        assertEquals(List.of(List.of(1L, 2L), List.of(3L, 4L)), Chunks.partition(List.of(1L, 2L, 3L, 4L), 2));
        assertEquals(List.of(List.of(1L, 2L), List.of(3L)), Chunks.partition(List.of(1L, 2L, 3L), 2));
        assertEquals(List.of(List.of(1L)), Chunks.partition(List.of(1L), 2));
    }

    @Test
    void removesDuplicatesKeepingOrder() {
        assertEquals(List.of(List.of(3L, 1L), List.of(2L)), Chunks.partition(List.of(3L, 1L, 3L, 2L, 1L), 2));
    }

    @Test
    void emptyInputMakesNoChunkAndNoQuery() {
        assertTrue(Chunks.partition(List.of(), 10).isEmpty());
        List<List<Long>> calls = new ArrayList<>();
        List<String> result = Chunks.lookup(List.of(), 10, chunk -> {
            calls.add(chunk);
            return List.of("x");
        });
        assertTrue(result.isEmpty());
        assertTrue(calls.isEmpty(), "빈 목록으로 IN 쿼리를 보내면 안 된다");
    }

    @Test
    void lookupCallsTheQueryOncePerChunk() {
        List<List<Long>> calls = new ArrayList<>();
        List<Long> result = Chunks.lookup(List.of(1L, 2L, 3L, 4L, 5L), 2, chunk -> {
            calls.add(chunk);
            return chunk;
        });
        assertEquals(3, calls.size());
        assertEquals(List.of(1L, 2L, 3L, 4L, 5L), result);
    }

    @Test
    void invalidArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> Chunks.partition(null, 2));
        assertThrows(IllegalArgumentException.class, () -> Chunks.partition(List.of(1L), 0));
    }
}
