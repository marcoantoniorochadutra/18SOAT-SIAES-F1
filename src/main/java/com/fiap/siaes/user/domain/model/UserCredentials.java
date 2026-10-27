package com.fiap.siaes.user.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import lombok.Builder;

@Builder
public record UserCredentials(UserId id, CustomerId customerId, String email, String password, UserRole role) {
}
