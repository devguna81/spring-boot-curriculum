package com.devguna.curriculum.ch04;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 조회 API.
 *
 * <p>{@code @RequestMapping("/users")}는 이 클래스의 모든 메서드 경로 앞에 붙는 공통 접두사다.
 * 이 챕터에는 {@code @GetMapping}만 있으므로 GET 이외의 메서드로 호출하면 405(Method Not Allowed)가 돌아온다.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStore store;

    public UserController(UserStore store) {
        this.store = store;
    }

    /**
     * GET /users, GET /users?keyword=kim, GET /users?keyword=kim&limit=1
     * 쿼리 문자열은 {@code @RequestParam}으로 받는다. 선택 값은 required=false 또는 defaultValue로 표현한다.
     */
    @GetMapping
    public List<User> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "10") int limit) {
        return store.findAll().stream()
                .filter(user -> keyword == null || user.name().contains(keyword))
                .limit(limit)
                .toList();
    }

    /**
     * GET /users/1
     * 경로의 일부는 {@code @PathVariable}로 받는다. 리소스가 없을 때는 200이 아니라 404를 돌려주는 것이 REST의 약속이다.
     * 상태 코드를 직접 정해야 하므로 반환 타입을 ResponseEntity로 한다.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable long id) {
        return store.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
