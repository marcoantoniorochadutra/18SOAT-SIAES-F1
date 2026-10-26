package com.fiap.siaes.auth.application.dto;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public record AuthenticatedUser(UserId id, CustomerId customerId, String email, UserRole userRole) {

    public static AuthenticatedUser of(UserId id, CustomerId customerId, String email, UserRole userRole) {
        return new AuthenticatedUser(id, customerId, email, userRole);
    }

    public boolean isCustomer() {
        return nonNull(this.userRole) && this.userRole.isCustomer();
    }

    public boolean isDifferentCustomer(CustomerId customerId) {
        return isNull(this.customerId) || !this.customerId.equals(customerId);
    }
}
