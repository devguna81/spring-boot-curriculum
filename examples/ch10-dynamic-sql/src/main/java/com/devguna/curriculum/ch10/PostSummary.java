package com.devguna.curriculum.ch10;

/** 목록의 한 행. 작성자는 JOIN으로 함께 읽어 중첩 객체로 담는다. */
public record PostSummary(long id, String title, String status, Author author) {

    public record Author(long id, String name) {
    }
}
