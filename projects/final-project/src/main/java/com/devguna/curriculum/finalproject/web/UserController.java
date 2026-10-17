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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
class UserController {

    private final UserApi userApi;
    private final CurrentActor currentActor;

    UserController(UserApi userApi, CurrentActor currentActor) {
        this.userApi = userApi;
        this.currentActor = currentActor;
    }

    @PostMapping
    ResponseEntity<UserView> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userApi.signup(request));
    }

    @GetMapping("/me")
    UserView me(HttpServletRequest request) {
        long id = currentActor.require(request).id();
        return userApi.findById(id).orElseThrow();
    }
}
