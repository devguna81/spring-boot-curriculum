package com.devguna.curriculum.ch12;

import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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
        return mapper.selectById(user.getId());
    }

    public List<User> findAll() {
        return mapper.selectList(new LambdaQueryWrapper<User>().orderByAsc(User::getId));
    }

    public Optional<User> findById(long id) {
        return Optional.ofNullable(mapper.selectById(id));
    }

    /** updateById는 null이 아닌 필드만 SET에 넣는다. 이 서비스는 전체 수정이라 모든 필드를 채워 보낸다. */
    public Optional<User> update(long id, UserRequest request) {
        User user = new User(request.name(), request.email(), request.age());
        user.setId(id);
        return mapper.updateById(user) == 0 ? Optional.empty() : Optional.of(mapper.selectById(id));
    }

    public boolean delete(long id) {
        return mapper.deleteById(id) > 0;
    }

    /** 값이 있는 조건만 WHERE에 들어간다. 컬럼 이름은 문자열이 아니라 메서드 참조로 지정한다. */
    public PageResponse<UserResponse> search(String name, Integer minAge, Integer maxAge, long page, long size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .like(name != null && !name.isBlank(), User::getName, name)
                .ge(minAge != null, User::getAge, minAge)
                .le(maxAge != null, User::getAge, maxAge)
                .orderByAsc(User::getId);
        // MyBatis-Plus의 Page는 1부터 시작한다. API는 0부터 받으므로 여기서 맞춘다.
        Page<User> result = mapper.selectPage(new Page<>(page + 1, size), wrapper);
        List<UserResponse> items = result.getRecords().stream().map(UserResponse::from).toList();
        return new PageResponse<>(items, page, size, result.getTotal(), result.getPages());
    }

    public List<User> findOlderThan(int age) {
        return mapper.findOlderThan(age);
    }
}
