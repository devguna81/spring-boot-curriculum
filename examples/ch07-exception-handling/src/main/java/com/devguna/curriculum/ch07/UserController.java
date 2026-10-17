package com.devguna.curriculum.ch07;

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

/** 성공 응답은 ApiResponse로 감싸고, 실패는 예외로 던진다. */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
        User created = service.create(request);
        return ResponseEntity.created(URI.create("/users/" + created.id()))
                .body(ApiResponse.ok(UserResponse.from(created)));
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> list(@RequestParam(defaultValue = "10") @Min(1) int limit) {
        return ApiResponse.ok(service.list().stream().limit(limit).map(UserResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> get(@PathVariable @Positive long id) {
        return ApiResponse.ok(UserResponse.from(service.get(id)));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(
            @PathVariable @Positive long id, @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.ok(UserResponse.from(service.update(id, request)));
    }

    @PatchMapping("/{id}")
    public ApiResponse<UserResponse> patch(
            @PathVariable @Positive long id, @Valid @RequestBody PatchUserRequest request) {
        return ApiResponse.ok(UserResponse.from(service.patch(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
