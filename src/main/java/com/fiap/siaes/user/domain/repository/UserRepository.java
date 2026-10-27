package com.fiap.siaes.user.domain.repository;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.UserCredentials;
import com.fiap.siaes.user.domain.model.vo.UserId;

import java.util.Optional;

public interface UserRepository extends RepositoryBase<User, UserId> {

    Optional<UserCredentials> findCredentialsByEmail(String email);

    Optional<UserCredentials> findCredentialsById(UserId userId);

    boolean existsByEmail(String email);

    Optional<User> findByRefreshToken(String refreshTokenHash);

    void deleteAllByCustomerId(CustomerId customerId);
}
