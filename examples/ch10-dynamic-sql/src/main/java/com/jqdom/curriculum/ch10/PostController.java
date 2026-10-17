package com.jqdom.curriculum.ch10;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    /** 예: /posts?keyword=Spring&status=PUBLISHED&status=DRAFT&sort=title&page=0&size=5 */
    @GetMapping("/posts")
    public PageResponse<PostSummary> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long authorId,
            @RequestParam(name = "status", required = false) List<String> statuses,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int size) {
        return service.search(new PostSearch(keyword, authorId, statuses, sort), page, size);
    }

    @GetMapping("/users/{id}/posts")
    public ResponseEntity<UserWithPosts> userWithPosts(@PathVariable long id) {
        UserWithPosts result = service.findUserWithPosts(id);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }
}
