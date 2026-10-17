package com.jqdom.curriculum.ch18.web;

import java.net.URI;
import java.util.List;

import com.jqdom.curriculum.ch18.contract.CreateUserRequest;
import com.jqdom.curriculum.ch18.contract.ErrorBody;
import com.jqdom.curriculum.ch18.contract.UserView;
import com.jqdom.curriculum.ch18.internal.UserApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 경로의 /api/v1이 계약의 버전이다. 호환되지 않는 변경은 /api/v2로 새로 연다. */
@Tag(name = "Users")
@RestController
@RequestMapping(path = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserApplicationService service;

    public UserController(UserApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "사용자 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성됨"),
            @ApiResponse(responseCode = "400", description = "검증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorBody.class))),
            @ApiResponse(responseCode = "409", description = "이메일 중복",
                    content = @Content(schema = @Schema(implementation = ErrorBody.class)))
    })
    @PostMapping
    public ResponseEntity<UserView> create(@Valid @RequestBody CreateUserRequest request) {
        UserView created = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/users/" + created.id())).body(created);
    }

    @Operation(summary = "사용자 목록")
    @GetMapping
    public List<UserView> list() {
        return service.list();
    }

    @Operation(summary = "사용자 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자"),
            @ApiResponse(responseCode = "404", description = "없는 사용자",
                    content = @Content(schema = @Schema(implementation = ErrorBody.class)))
    })
    @GetMapping("/{id}")
    public UserView get(@PathVariable long id) {
        return service.get(id);
    }
}
