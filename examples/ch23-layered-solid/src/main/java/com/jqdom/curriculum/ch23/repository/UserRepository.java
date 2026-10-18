package com.jqdom.curriculum.ch23.repository;

import com.jqdom.curriculum.ch23.domain.User;

import java.util.Optional;

/** 서비스가 의존하는 것은 이 인터페이스다. 저장 방식은 구현 클래스가 정한다. */
public interface UserRepository {

    User save(String name, String email);

    Optional<User> findById(long id);

    boolean existsByEmail(String email);
}
