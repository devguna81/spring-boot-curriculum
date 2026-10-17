package com.devguna.curriculum.ch18.internal;

import java.util.List;

import com.devguna.curriculum.ch18.contract.CreateUserRequest;
import com.devguna.curriculum.ch18.contract.UserView;

import org.springframework.stereotype.Service;

/**
 * web 계층이 호출하는 유일한 입구. 받고 돌려주는 타입이 모두 계약 객체라서
 * web은 UserEntity나 UserRepository를 알 필요가 없다.
 */
@Service
public class UserApplicationService {

    private final UserRepository repository;
    private final UserContractMapper mapper;

    UserApplicationService(UserRepository repository, UserContractMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public UserView create(CreateUserRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        UserEntity entity = mapper.toEntity(request);
        // 내부에서만 쓰는 값은 서비스가 채운다. 계약에는 나가지 않는다.
        UserEntity withMemo = new UserEntity(null, entity.name(), entity.email(), entity.age(), "created-via-api", false);
        return mapper.toView(repository.save(withMemo));
    }

    public UserView get(long id) {
        return repository.findById(id).map(mapper::toView).orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<UserView> list() {
        return mapper.toViews(repository.findAll());
    }
}
