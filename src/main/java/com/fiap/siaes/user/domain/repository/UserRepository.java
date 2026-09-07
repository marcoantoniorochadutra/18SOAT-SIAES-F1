package com.fiap.siaes.user.domain.repository;

import com.fiap.siaes.user.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
