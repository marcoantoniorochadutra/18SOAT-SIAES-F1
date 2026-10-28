package com.fiap.siaes.customer.domain.event;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.domain.event.DomainEvent;
import com.fiap.siaes.user.domain.model.vo.UserId;

import java.time.Instant;

public record CustomerDeletedEvent(CustomerId customerId, UserId deletedBy, Instant occurredAt) implements DomainEvent {

    public static CustomerDeletedEvent of(CustomerId customerId, UserId deletedBy, Instant occurredAt) {
        return new CustomerDeletedEvent(customerId, deletedBy, occurredAt);
    }
}
