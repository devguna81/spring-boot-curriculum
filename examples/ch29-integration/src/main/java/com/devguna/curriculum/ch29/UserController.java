package com.devguna.curriculum.ch29;

import java.net.URI;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User created = service.create(request);
        return ResponseEntity.created(URI.create("/users/" + created.getId())).body(UserResponse.from(created));
    }

    @GetMapping
    public List<UserResponse> list() {
        return service.findAll().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable long id) {
        return service.findById(id).map(UserResponse::from).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable long id, @Valid @RequestBody UserRequest request) {
        return service.update(id, request).map(UserResponse::from).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * UNIQUE 제약 위반은 MyBatis 예외가 아니라 Spring의 DuplicateKeyException으로 번역되어 올라온다.
     * 데이터베이스 종류와 관계없이 같은 예외로 받을 수 있다.
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> duplicate() {
        return Map.of("code", "DUPLICATE_EMAIL", "message", "이미 사용 중인 이메일입니다");
    }
}
