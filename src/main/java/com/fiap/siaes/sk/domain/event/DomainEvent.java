package com.fiap.siaes.sk.domain.event;

import java.time.Instant;

public interface DomainEvent {

    Instant occurredAt();
}
