package com.fiap.siaes.quotation.domain.event;

import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.sk.domain.event.DomainEvent;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

import java.time.Instant;

public record QuotationCompletedEvent(QuotationId quotationId, WorkOrderId workOrderId, Instant occurredAt) implements DomainEvent {

    public static QuotationCompletedEvent of(QuotationId quotationId, WorkOrderId workOrderId, Instant occurredAt) {
        return new QuotationCompletedEvent(quotationId, workOrderId, occurredAt);
    }
}
