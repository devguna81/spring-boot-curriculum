package com.devguna.curriculum.finalproject.user.internal;

import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.devguna.curriculum.finalproject.contract.Role;
import com.devguna.curriculum.finalproject.contract.SignupRequest;
import com.devguna.curriculum.finalproject.contract.UserView;
import com.devguna.curriculum.finalproject.user.api.DuplicateUsernameException;
import com.devguna.curriculum.finalproject.user.api.UserAccount;
import com.devguna.curriculum.finalproject.user.api.UserApi;

@Service
class UserService implements UserApi {

    private final UserMapper mapper;
    private final UserConverter converter;
    private final PasswordEncoder passwordEncoder;

    UserService(UserMapper mapper, UserConverter converter, PasswordEncoder passwordEncoder) {
        this.mapper = mapper;
        this.converter = converter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserView signup(SignupRequest request) {
        return createAccount(request.username(), request.password(), request.displayName(), Role.USER);
    }

    @Override
    @Transactional
    public UserView createAccount(String username, String rawPassword, String displayName, Role role) {
        if (mapper.exists(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, username))) {
            throw new DuplicateUsernameException(username);
        }
        UserEntity entity = new UserEntity(username, passwordEncoder.encode(rawPassword), displayName, role);
        try {
            mapper.insert(entity);
        } catch (DuplicateKeyException e) {
            // 위의 확인과 INSERT 사이에 같은 아이디가 들어온 경우. UNIQUE 제약이 마지막 방어선이다.
            throw new DuplicateUsernameException(username);
        }
        return converter.toView(entity);
    }

    @Override
    public Optional<UserView> findById(long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(converter::toView);
    }

    @Override
    public boolean exists(long id) {
        return mapper.selectById(id) != null;
    }

    @Override
    public Optional<UserAccount> findAccount(String username) {
        UserEntity entity = mapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, username));
        return Optional.ofNullable(entity).map(converter::toAccount);
    }
}
