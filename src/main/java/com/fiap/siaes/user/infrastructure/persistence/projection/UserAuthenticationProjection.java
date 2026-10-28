package com.fiap.siaes.user.infrastructure.persistence.projection;

import com.fiap.siaes.user.domain.model.enums.UserRole;

import java.util.UUID;

public interface UserAuthenticationProjection {

    UUID getId();
    UUID getCustomerId();
    String getPassword();
    String getEmail();
    String getRole();

    default UserRole getUserRole() {
        return UserRole.valueOf(this.getRole());
    }
}
