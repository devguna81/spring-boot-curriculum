package com.jqdom.curriculum.finalproject.post.api;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(long id) {
        super("글을 찾을 수 없습니다: " + id);
    }
}
