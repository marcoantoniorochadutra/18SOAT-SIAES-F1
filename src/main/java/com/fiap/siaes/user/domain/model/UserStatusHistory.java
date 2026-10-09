package com.fiap.siaes.user.domain.model;

import com.fiap.siaes.user.domain.model.enums.UserStatus;
import com.fiap.siaes.user.domain.model.vo.UserStatusHistoryId;
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
    private String observation;

    public static UserStatusHistory of(UserStatus status) {
        return new UserStatusHistory(UserStatusHistoryId.generate(), status, Instant.now(), null);
    }

}
