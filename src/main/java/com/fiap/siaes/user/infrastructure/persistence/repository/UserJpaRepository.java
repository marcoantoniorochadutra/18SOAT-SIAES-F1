package com.fiap.siaes.user.infrastructure.persistence.repository;

import com.fiap.siaes.user.domain.model.UserId;
import com.fiap.siaes.user.infrastructure.persistence.entity.UserJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpa, UserId> {

    Optional<UserJpa> findByEmail(String email);

    boolean existsByEmail(String email);
}
