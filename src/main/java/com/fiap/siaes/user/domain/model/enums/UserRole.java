package com.fiap.siaes.user.domain.model.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN(1000),
    MANAGER(500),
    MECHANIC(300),
    CUSTOMER(100);

    private final int hierarchy;

    public boolean hasAccessTo(UserRole requiredRole) {
        return this.hierarchy >= requiredRole.hierarchy;
    }
}
