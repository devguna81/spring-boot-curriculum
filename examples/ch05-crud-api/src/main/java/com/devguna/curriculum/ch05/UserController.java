package com.devguna.curriculum.ch05;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP 메서드와 CRUD의 대응.
 * <pre>
 * POST   /users      생성        201 Created + Location 헤더
 * GET    /users      목록        200
 * GET    /users/{id} 단건 조회    200 / 404
 * PUT    /users/{id} 전체 교체    200 / 404   (같은 요청을 반복해도 결과가 같다: 멱등)
 * PATCH  /users/{id} 부분 수정    200 / 404
 * DELETE /users/{id} 삭제        204 No Content / 404
 * </pre>
 * {@code @RequestBody}는 요청 본문의 JSON을 객체로 바꾼다. 응답 객체는 JSON으로 바뀌어 본문에 쓰인다.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest request) {
        User created = service.create(request);
        return ResponseEntity
                .created(URI.create("/users/" + created.id()))
                .body(UserResponse.from(created));
    }

    @GetMapping
    public List<UserResponse> list() {
        return service.list().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable long id) {
        return service.find(id).map(UserResponse::from).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable long id, @RequestBody UpdateUserRequest request) {
        return service.update(id, request).map(UserResponse::from).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patch(@PathVariable long id, @RequestBody PatchUserRequest request) {
        return service.patch(id, request).map(UserResponse::from).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
