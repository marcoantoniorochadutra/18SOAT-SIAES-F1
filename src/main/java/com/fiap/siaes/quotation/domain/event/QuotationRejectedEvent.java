package com.fiap.siaes.quotation.domain.event;

import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.sk.domain.event.DomainEvent;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

import java.time.Instant;

public record QuotationRejectedEvent(QuotationId quotationId, WorkOrderId workOrderId, Instant occurredAt) implements DomainEvent {

    public static QuotationRejectedEvent of(QuotationId quotationId, WorkOrderId workOrderId, Instant occurredAt) {
        return new QuotationRejectedEvent(quotationId, workOrderId, occurredAt);
    }
}
