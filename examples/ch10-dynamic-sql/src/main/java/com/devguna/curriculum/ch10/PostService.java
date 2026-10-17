package com.devguna.curriculum.ch10;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostMapper mapper;

    public PostService(PostMapper mapper) {
        this.mapper = mapper;
    }

    public PageResponse<PostSummary> search(PostSearch cond, int page, int size) {
        long total = mapper.count(cond);
        // 총 건수가 0이면 목록 조회를 건너뛴다.
        var items = total == 0 ? List.<PostSummary>of() : mapper.search(cond, page * size, size);
        return PageResponse.of(items, page, size, total);
    }

    public UserWithPosts findUserWithPosts(long userId) {
        return mapper.findUserWithPosts(userId);
    }
}
