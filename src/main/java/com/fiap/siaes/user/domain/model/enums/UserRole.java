package com.fiap.siaes.user.domain.model.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.util.Arrays.stream;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN(1000),
    MANAGER(500),
    MECHANIC(300),
    CUSTOMER(300);

    private final int hierarchy;

    public static UserRole fromOrdinal(Integer role) {
        return stream(values())
                .filter(userRole -> userRole.ordinal() == role)
                .findFirst()
                .orElse(null);
    }

    public boolean lacksAccessTo(UserRole requiredRole) {
        return this.hierarchy < requiredRole.hierarchy;
    }

    public boolean isCustomer() {
        return CUSTOMER.equals(this);
    }
}
