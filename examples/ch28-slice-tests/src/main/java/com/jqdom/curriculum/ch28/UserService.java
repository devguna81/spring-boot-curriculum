package com.jqdom.curriculum.ch28;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper mapper;

    public UserService(UserMapper mapper) {
        this.mapper = mapper;
    }

    public User create(UserRequest request) {
        User user = new User(request.name(), request.email(), request.age());
        mapper.insert(user);
        // INSERT 직후에는 created_at이 객체에 없으므로 다시 읽어 온다.
        return mapper.findById(user.getId());
    }

    public List<User> findAll() {
        return mapper.findAll();
    }

    public Optional<User> findById(long id) {
        return Optional.ofNullable(mapper.findById(id));
    }

    public Optional<User> update(long id, UserRequest request) {
        User user = new User(request.name(), request.email(), request.age());
        user.setId(id);
        // 갱신된 행이 0이면 해당 id가 없다는 뜻이다.
        return mapper.update(user) == 0 ? Optional.empty() : Optional.of(mapper.findById(id));
    }

    public boolean delete(long id) {
        return mapper.deleteById(id) > 0;
    }
}
