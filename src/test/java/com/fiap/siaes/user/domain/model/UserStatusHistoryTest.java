package com.fiap.siaes.user.domain.model;

import com.fiap.siaes.user.domain.model.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - UserStatusHistory")
class UserStatusHistoryTest {

    @Test
    @DisplayName("Deve criar um novo registro de histórico com id, status e data atuais")
    void shouldCreateHistoryEntryForStatus() {
        UserStatusHistory history = UserStatusHistory.of(UserStatus.SUSPENDED);

        assertNotNull(history.getId());
        assertEquals(UserStatus.SUSPENDED, history.getStatus());
        assertTrue(Duration.between(history.getUpdatedAt(), Instant.now()).abs().getSeconds() <= 2);
        assertNull(history.getObservation());
    }
}
