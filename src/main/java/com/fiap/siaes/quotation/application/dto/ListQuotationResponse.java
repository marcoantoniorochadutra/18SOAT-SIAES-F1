package com.fiap.siaes.quotation.application.dto;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record ListQuotationResponse(QuotationId id, WorkOrderId workOrderId, CustomerReferenceResponse customer,
                                    VehicleReferenceResponse vehicle, String description, QuotationStatus status,
                                    BigDecimal totalAmount, Instant createdAt) {
}
