package com.devguna.curriculum.finalproject.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devguna.curriculum.finalproject.contract.PageResponse;
import com.devguna.curriculum.finalproject.contract.PostRequest;
import com.devguna.curriculum.finalproject.contract.PostSummary;
import com.devguna.curriculum.finalproject.contract.PostView;
import com.devguna.curriculum.finalproject.post.api.PostApi;
import com.devguna.curriculum.finalproject.post.api.PostQuery;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/posts")
class PostController {

    private final PostApi postApi;
    private final CurrentActor currentActor;

    PostController(PostApi postApi, CurrentActor currentActor) {
        this.postApi = postApi;
        this.currentActor = currentActor;
    }

    @GetMapping
    PageResponse<PostSummary> search(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Long authorId,
                                     @RequestParam(required = false) String tag,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return postApi.search(new PostQuery(keyword, authorId, tag, page, size));
    }

    @GetMapping("/{id}")
    PostView get(@PathVariable long id) {
        return postApi.get(id);
    }

    @PostMapping
    ResponseEntity<PostView> create(HttpServletRequest http, @Valid @RequestBody PostRequest request) {
        PostView created = postApi.create(currentActor.require(http), request);
        return ResponseEntity.created(URI.create("/api/v1/posts/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    PostView update(HttpServletRequest http, @PathVariable long id, @Valid @RequestBody PostRequest request) {
        return postApi.update(currentActor.require(http), id, request);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(HttpServletRequest http, @PathVariable long id) {
        postApi.delete(currentActor.require(http), id);
        return ResponseEntity.noContent().build();
    }
}
