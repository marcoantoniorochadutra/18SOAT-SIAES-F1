package com.fiap.siaes.supplies.domain.event;

import com.fiap.siaes.sk.domain.event.DomainEvent;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;

import java.time.Instant;

public record SuppliesCreatedEvent(SuppliesId suppliesId, int initialStock,
                                   UserId createdBy, Instant occurredAt) implements DomainEvent {

    public static SuppliesCreatedEvent of(SuppliesId suppliesId, int initialStock, UserId createdBy,
                                          Instant occurredAt) {
        return new SuppliesCreatedEvent(suppliesId, initialStock, createdBy, occurredAt);
    }
}
