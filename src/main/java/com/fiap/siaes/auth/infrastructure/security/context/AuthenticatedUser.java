package com.fiap.siaes.auth.infrastructure.security.context;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.infrastructure.persistence.projection.UserAuthenticationProjection;
import lombok.Builder;

@Builder
public record AuthenticatedUser(UserId id, CustomerId customerId, String email, UserRole userRole) {

    public static AuthenticatedUser from(UserAuthenticationProjection user) {
        return new AuthenticatedUser(UserId.from(user.getId()), CustomerId.from(user.getCustomerId()), user.getEmail(), user.getUserRole());
    }
}
