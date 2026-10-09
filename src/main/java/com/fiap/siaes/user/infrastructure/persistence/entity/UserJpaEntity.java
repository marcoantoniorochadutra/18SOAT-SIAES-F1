package com.fiap.siaes.user.infrastructure.persistence.entity;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.enums.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class UserJpaEntity {

    @Id
    private UserId id;

    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.ORDINAL)
    private UserRole role;

    @Enumerated(EnumType.ORDINAL)
    private UserStatus lastStatus;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false)
    private Set<UserStatusHistoryJpaEntity> statusHistory;

    private CustomerId customerId;
    private Instant lastLoginAt;
    private String refreshToken;
}
