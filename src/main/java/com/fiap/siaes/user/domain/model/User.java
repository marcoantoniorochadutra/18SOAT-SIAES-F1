package com.fiap.siaes.user.domain.model;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UserId id;
    private String name;
    private String email;
    private String password;
    private UserRole role;

    private UserStatus lastStatus;
    private Set<UserStatusHistory> statusHistory;

    private CustomerId customerId;
    private Instant lastLoginAt;
    private String refreshToken;
}
