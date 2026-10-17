package com.devguna.curriculum.ch24.application.port;

import com.devguna.curriculum.ch24.domain.Address;
import com.devguna.curriculum.ch24.domain.Member;

import java.util.Optional;

public interface MemberStore {

    Member save(String name, Address address);

    Optional<Member> findById(long id);
}
