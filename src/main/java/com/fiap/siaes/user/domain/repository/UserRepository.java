package com.fiap.siaes.user.domain.repository;

import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import com.fiap.siaes.user.domain.exception.UserNotFoundException;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.infrastructure.persistence.projection.UserAuthenticationProjection;

import static java.util.Objects.isNull;

public interface UserRepository extends RepositoryBase<User, UserId> {

    UserAuthenticationProjection findByEmail(String email);

    default UserAuthenticationProjection findByEmailOrThrowNotFound(String email) {
        UserAuthenticationProjection user = this.findByEmail(email);

        if (isNull(user))
            throw new UserNotFoundException(email);

        return user;
    }

    UserAuthenticationProjection findUserAuthById(UserId userId);

    boolean existsByEmail(String email);
}
