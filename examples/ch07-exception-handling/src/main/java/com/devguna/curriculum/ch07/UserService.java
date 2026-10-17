package com.devguna.curriculum.ch07;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * 이전 챕터까지는 "없으면 빈 값(Optional)"을 돌려주고 컨트롤러가 404로 바꿨다.
 * 이제 서비스가 의미 있는 예외를 던지고, 상태 코드와 응답 모양은 {@link GlobalExceptionHandler}가 정한다.
 * 그래서 컨트롤러에는 if/else도 ResponseEntity 조립도 사라진다.
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

    public User get(long id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User create(CreateUserRequest request) {
        assertEmailAvailable(request.email(), -1);
        return repository.save(request.name(), request.email(), request.age());
    }

    public User update(long id, UpdateUserRequest request) {
        get(id);
        assertEmailAvailable(request.email(), id);
        return repository.replace(new User(id, request.name(), request.email(), request.age())).orElseThrow();
    }

    public User patch(long id, PatchUserRequest request) {
        User current = get(id);
        String email = request.email() != null ? request.email() : current.email();
        assertEmailAvailable(email, id);
        return repository.replace(new User(
                id,
                request.name() != null ? request.name() : current.name(),
                email,
                request.age() != null ? request.age() : current.age())).orElseThrow();
    }

    public void delete(long id) {
        if (!repository.deleteById(id)) {
            throw new UserNotFoundException(id);
        }
    }

    /** 같은 이메일을 쓰는 다른 사용자가 있으면 거절한다. selfId는 수정 대상 자신을 제외하기 위한 값이다. */
    private void assertEmailAvailable(String email, long selfId) {
        repository.findByEmail(email)
                .filter(found -> found.id() != selfId)
                .ifPresent(found -> {
                    throw new DuplicateEmailException(email);
                });
    }
}
