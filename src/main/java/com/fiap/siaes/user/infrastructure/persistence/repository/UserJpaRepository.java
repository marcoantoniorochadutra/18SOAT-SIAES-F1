package com.fiap.siaes.user.infrastructure.persistence.repository;

import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.infrastructure.persistence.entity.UserJpaEntity;
import com.fiap.siaes.user.infrastructure.persistence.projection.UserAuthenticationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UserId> {

    @Query(value = """
        SELECT u.id AS id,
               u.customer_id AS customerId,
               u.email AS email,
               u.password AS password,
               u.role AS role
        FROM {h-schema} users u
        WHERE u.id = :userId
    """, nativeQuery = true)
    UserAuthenticationProjection findUserAuthById(UserId userId);

    @Query(value = """
        SELECT u.id AS id,
               u.customer_id AS customerId,
               u.email AS email,
               u.password AS password,
               u.role AS role
        FROM {h-schema} users u
        WHERE u.email = :email
    """, nativeQuery = true)
    UserAuthenticationProjection findByEmail(String email);

    @Query(value = """
        SELECT EXISTS (
           SELECT 1
           FROM {h-schema} users u
           WHERE u.email = :email)
    """, nativeQuery = true)
    boolean existsByEmail(String email);

}
