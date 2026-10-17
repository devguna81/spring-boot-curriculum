package com.jqdom.curriculum.ch23.web;

import com.jqdom.curriculum.ch23.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/** 요청을 받아 서비스를 부르고 응답 모양으로 바꾸는 일만 한다. 저장소는 직접 쓰지 않는다. */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        UserResponse body = UserResponse.from(service.register(request.name(), request.email()));
        return ResponseEntity.created(URI.create("/api/users/" + body.id())).body(body);
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable long id) {
        return UserResponse.from(service.get(id));
    }
}
