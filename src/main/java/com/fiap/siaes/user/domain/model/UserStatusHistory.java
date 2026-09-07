package com.fiap.siaes.user.domain.model;

import com.fiap.siaes.user.domain.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusHistory {

    private UserStatusHistoryId id;
    private UserStatus status;
    private Instant updatedAt;

}
