package com.devguna.curriculum.ch06;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * 컨트롤러와 저장소 사이의 계층. 요청을 받고 응답을 만드는 일(컨트롤러)과
 * 데이터를 보관하는 일(저장소) 사이에서 "무엇을 어떻게 바꿀지"를 정한다.
 */
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> list() {
        return repository.findAll();
    }

    public Optional<User> find(long id) {
        return repository.findById(id);
    }

    public User create(CreateUserRequest request) {
        return repository.save(request.name(), request.email(), request.age());
    }

    /** 전체 교체(PUT). */
    public Optional<User> update(long id, UpdateUserRequest request) {
        return repository.replace(new User(id, request.name(), request.email(), request.age()));
    }

    /** 부분 수정(PATCH). 값이 있는 필드만 바꾼다. */
    public Optional<User> patch(long id, PatchUserRequest request) {
        return repository.findById(id).flatMap(current -> repository.replace(new User(
                id,
                request.name() != null ? request.name() : current.name(),
                request.email() != null ? request.email() : current.email(),
                request.age() != null ? request.age() : current.age())));
    }

    public boolean delete(long id) {
        return repository.deleteById(id);
    }
}
