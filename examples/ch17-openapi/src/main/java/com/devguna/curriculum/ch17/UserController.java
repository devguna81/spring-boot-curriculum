package com.devguna.curriculum.ch17;

import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import org.springframework.http.MediaType;
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
@Tag(name = "Users", description = "사용자 등록, 조회, 수정, 삭제")
@RestController
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(summary = "사용자 생성", description = "이메일은 중복될 수 없다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성됨. Location 헤더에 새 주소"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "검증 실패 (details에 필드별 사유)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이메일 중복",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request) {
        User created = service.create(request);
        return ResponseEntity.created(URI.create("/users/" + created.id()))
                .body(ApiResponse.ok(UserResponse.from(created)));
    }

    @Operation(summary = "사용자 목록", description = "id 순서로 limit개까지 돌려준다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "목록"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "limit이 1 미만",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping
    public ApiResponse<List<UserResponse>> list(@RequestParam(defaultValue = "10") @Min(1) int limit) {
        return ApiResponse.ok(service.list().stream().limit(limit).map(UserResponse::from).toList());
    }

    @Operation(summary = "사용자 단건 조회", description = "")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "id가 양수가 아니거나 숫자가 아님",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> get(@PathVariable @Positive long id) {
        return ApiResponse.ok(UserResponse.from(service.get(id)));
    }

    @Operation(summary = "사용자 전체 교체", description = "모든 필드를 보내야 한다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "교체된 사용자"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "검증 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "다른 사용자가 쓰는 이메일",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(
            @PathVariable @Positive long id, @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.ok(UserResponse.from(service.update(id, request)));
    }

    @Operation(summary = "사용자 부분 수정", description = "보낸 필드만 변경한다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정된 사용자"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "검증 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "다른 사용자가 쓰는 이메일",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PatchMapping("/{id}")
    public ApiResponse<UserResponse> patch(
            @PathVariable @Positive long id, @Valid @RequestBody PatchUserRequest request) {
        return ApiResponse.ok(UserResponse.from(service.patch(id, request)));
    }

    @Operation(summary = "사용자 삭제", description = "")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제됨"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "없는 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
