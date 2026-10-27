package com.fiap.siaes.workorder.domain.event;

import com.fiap.siaes.sk.domain.event.DomainEvent;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

import java.time.Instant;

public record WorkOrderCancelledEvent(WorkOrderId workOrderId, UserId cancelledBy, Instant occurredAt) implements DomainEvent {

    public static WorkOrderCancelledEvent of(WorkOrderId workOrderId, UserId cancelledBy, Instant occurredAt) {
        return new WorkOrderCancelledEvent(workOrderId, cancelledBy, occurredAt);
    }
}
