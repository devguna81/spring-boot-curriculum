package com.devguna.curriculum.finalproject.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devguna.curriculum.finalproject.contract.SignupRequest;
import com.devguna.curriculum.finalproject.contract.UserView;
import com.devguna.curriculum.finalproject.user.api.UserApi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(name = "사용자")
@RestController
@RequestMapping("/api/v1/users")
class UserController {

    private final UserApi userApi;
    private final CurrentActor currentActor;

    UserController(UserApi userApi, CurrentActor currentActor) {
        this.userApi = userApi;
        this.currentActor = currentActor;
    }

    @Operation(summary = "회원 가입", security = {})
    @PostMapping
    ResponseEntity<UserView> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userApi.signup(request));
    }

    @Operation(summary = "내 정보")
    @GetMapping("/me")
    UserView me() {
        long id = currentActor.require().id();
        return userApi.findById(id).orElseThrow();
    }
}
