package com.jqdom.curriculum.ch06;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 검증이 일어나는 곳.
 * <ul>
 *   <li>{@code @Valid @RequestBody} - 요청 본문 객체의 제약을 검사한다. 실패하면 MethodArgumentNotValidException</li>
 *   <li>{@code @PathVariable @Positive}, {@code @RequestParam @Min} - 메서드 인자에 직접 붙인 제약.
 *       실패하면 HandlerMethodValidationException</li>
 * </ul>
 * 두 경우 모두 컨트롤러 메서드가 실행되기 전에 막히고, 기본 응답 상태는 400이다.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        User created = service.create(request);
        return ResponseEntity.created(URI.create("/users/" + created.id())).body(UserResponse.from(created));
    }

    @GetMapping
    public List<UserResponse> list(@RequestParam(defaultValue = "10") @Min(1) int limit) {
        return service.list().stream().limit(limit).map(UserResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable @Positive long id) {
        return service.find(id).map(UserResponse::from).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable @Positive long id, @Valid @RequestBody UpdateUserRequest request) {
        return service.update(id, request).map(UserResponse::from).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patch(
            @PathVariable @Positive long id, @Valid @RequestBody PatchUserRequest request) {
        return service.patch(id, request).map(UserResponse::from).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
