package com.fiap.siaes.user.infrastructure.persistence.entity;

import com.fiap.siaes.user.domain.model.enums.UserStatus;
import com.fiap.siaes.user.domain.model.vo.UserStatusHistoryId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_status_history")
public class UserStatusHistoryJpaEntity {

    @Id
    private UserStatusHistoryId id;

    @Enumerated(EnumType.ORDINAL)
    private UserStatus status;

    private Instant updatedAt;

    @Column(name = "observation", length = 150)
    private String observation;
}
