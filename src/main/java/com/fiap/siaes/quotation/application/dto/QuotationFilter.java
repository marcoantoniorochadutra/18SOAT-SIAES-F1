package com.fiap.siaes.quotation.application.dto;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

public record QuotationFilter(WorkOrderId workOrderId, CustomerId customerId, VehicleId vehicleId, QuotationStatus status) {

    public static QuotationFilter of(WorkOrderId workOrderId, CustomerId customerId, VehicleId vehicleId,
                                     QuotationStatus status) {
        return new QuotationFilter(workOrderId, customerId, vehicleId, status);
    }

    public QuotationFilter withCustomerId(CustomerId customerId) {
        return of(this.workOrderId, customerId, this.vehicleId, this.status);
    }
}
