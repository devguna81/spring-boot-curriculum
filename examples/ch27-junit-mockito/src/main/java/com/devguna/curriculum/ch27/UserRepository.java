package com.devguna.curriculum.ch27;

import java.util.Optional;

public interface UserRepository {

    boolean existsByEmail(String email);

    User save(String name, String email, java.time.LocalDate joinedOn);

    Optional<User> findById(long id);
}
