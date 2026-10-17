package com.jqdom.curriculum.finalproject.web;

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

import com.jqdom.curriculum.finalproject.contract.PageResponse;
import com.jqdom.curriculum.finalproject.contract.PostRequest;
import com.jqdom.curriculum.finalproject.contract.PostSummary;
import com.jqdom.curriculum.finalproject.contract.PostView;
import com.jqdom.curriculum.finalproject.post.api.PostApi;
import com.jqdom.curriculum.finalproject.post.api.PostQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(name = "게시글")
@RestController
@RequestMapping("/api/v1/posts")
class PostController {

    private final PostApi postApi;
    private final CurrentActor currentActor;

    PostController(PostApi postApi, CurrentActor currentActor) {
        this.postApi = postApi;
        this.currentActor = currentActor;
    }

    @Operation(summary = "글 검색", description = "고정 글이 먼저, 그다음 최신순. size는 1~50.")
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

    @Operation(summary = "글 작성", description = "로그인이 필요하다.")
    @PostMapping
    ResponseEntity<PostView> create(@Valid @RequestBody PostRequest request) {
        PostView created = postApi.create(currentActor.require(), request);
        return ResponseEntity.created(URI.create("/api/v1/posts/" + created.id())).body(created);
    }

    @Operation(summary = "글 수정", description = "작성자 또는 관리자만 할 수 있다.")
    @PutMapping("/{id}")
    PostView update(@PathVariable long id, @Valid @RequestBody PostRequest request) {
        return postApi.update(currentActor.require(), id, request);
    }

    @Operation(summary = "글 삭제", description = "작성자 또는 관리자만 할 수 있다.")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable long id) {
        postApi.delete(currentActor.require(), id);
        return ResponseEntity.noContent().build();
    }
}
