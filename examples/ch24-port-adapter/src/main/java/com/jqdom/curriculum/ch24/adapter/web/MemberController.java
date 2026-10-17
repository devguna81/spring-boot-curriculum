package com.jqdom.curriculum.ch24.adapter.web;

import com.jqdom.curriculum.ch24.application.usecase.RegisterMemberUseCase;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final RegisterMemberUseCase useCase;

    public MemberController(RegisterMemberUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<MemberResponse> register(@Valid @RequestBody MemberRequest request) {
        MemberResponse body = MemberResponse.from(useCase.register(request.name(), request.postalCode()));
        return ResponseEntity.created(URI.create("/api/members/" + body.id())).body(body);
    }

    @GetMapping("/{id}")
    public MemberResponse get(@PathVariable long id) {
        return MemberResponse.from(useCase.get(id));
    }
}
