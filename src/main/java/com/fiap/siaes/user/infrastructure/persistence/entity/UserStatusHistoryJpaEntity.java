package com.fiap.siaes.user.infrastructure.persistence.entity;

import com.fiap.siaes.user.domain.model.UserStatusHistoryId;
import com.fiap.siaes.user.domain.model.enums.UserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusHistoryJpaEntity {

    @Id
    private UserStatusHistoryId id;

    @Enumerated(EnumType.ORDINAL)
    private UserStatus status;

    private Instant updatedAt;
}
