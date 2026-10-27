package com.fiap.siaes.user.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.exception.UserCustomerRelationRequiredException;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.enums.UserStatus;
import com.fiap.siaes.user.domain.model.vo.Password;
import com.fiap.siaes.user.domain.model.vo.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
public class User {

    private UserId id;
    private String name;
    private String email;
    private Password password;
    private UserRole role;

    private UserStatus lastStatus;
    private Set<UserStatusHistory> statusHistory;

    private CustomerId customerId;

    private Instant lastLoginAt;
    private String refreshToken;
    private Instant refreshTokenExpiresAt;

    @Builder
    public User(UserId id, String name, String email, Password password, UserRole role, CustomerId customerId) {
        this.validateUserCustomer(role, customerId);

        this.id = isNull(id) ? UserId.generate() : id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.customerId = customerId;

        this.updateStatus(UserStatus.ACTIVE);
    }

    private void validateUserCustomer(UserRole role, CustomerId customerId) {
        if (UserRole.CUSTOMER.equals(role) && isNull(customerId)) {
            throw new UserCustomerRelationRequiredException();
        }
    }

    @Builder(builderMethodName = "recreate", builderClassName = "RecreateBuilder")
    public User(UserId id, String name, String email, Password password, UserRole role, UserStatus lastStatus,
                Set<UserStatusHistory> statusHistory, CustomerId customerId, Instant lastLoginAt,
                String refreshToken, Instant refreshTokenExpiresAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.lastStatus = lastStatus;
        this.statusHistory = statusHistory;
        this.customerId = customerId;
        this.lastLoginAt = lastLoginAt;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public void startSession(String refreshTokenHash, Instant refreshTokenExpiresAt) {
        this.lastLoginAt = Instant.now();
        this.rotateRefreshToken(refreshTokenHash, refreshTokenExpiresAt);
    }

    public void rotateRefreshToken(String refreshTokenHash, Instant refreshTokenExpiresAt) {
        this.refreshToken = refreshTokenHash;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public void endSession() {
        this.refreshToken = null;
        this.refreshTokenExpiresAt = null;
    }

    public boolean hasValidRefreshToken(Instant now) {
        return nonNull(this.refreshToken) && nonNull(this.refreshTokenExpiresAt) && this.refreshTokenExpiresAt.isAfter(now);
    }

    private void updateStatus(UserStatus newStatus) {
        if (nonNull(this.lastStatus) && this.lastStatus.equals(newStatus)) {
            return;
        }

        this.lastStatus = newStatus;

        if (isNull(this.statusHistory))
            this.statusHistory = new HashSet<>();

        this.statusHistory.add(UserStatusHistory.of(newStatus));
    }

    public Set<UserStatusHistory> getStatusHistory() {
        return isNull(this.statusHistory) ? Set.of() : Collections.unmodifiableSet(this.statusHistory);
    }
}
