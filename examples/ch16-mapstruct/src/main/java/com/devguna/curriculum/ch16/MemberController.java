package com.devguna.curriculum.ch16;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 컨트롤러는 도메인 객체를 응답으로 내보내지 않고, 항상 매퍼를 거친 응답 DTO만 내보낸다. */
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService service;
    private final MemberMapper mapper;

    public MemberController(MemberService service, MemberMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<MemberResponse> create(@Valid @RequestBody CreateMemberRequest request) {
        Member created = service.create(request);
        return ResponseEntity.created(URI.create("/members/" + created.getId())).body(mapper.toResponse(created));
    }

    @GetMapping
    public List<MemberResponse> list() {
        return mapper.toResponses(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> get(@PathVariable long id) {
        return service.findById(id).map(mapper::toResponse).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MemberResponse> patch(@PathVariable long id, @Valid @RequestBody PatchMemberRequest request) {
        return service.patch(id, request).map(mapper::toResponse).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
