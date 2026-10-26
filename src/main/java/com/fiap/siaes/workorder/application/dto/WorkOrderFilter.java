package com.fiap.siaes.workorder.application.dto;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.document.domain.Document;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;

public record WorkOrderFilter(CustomerId customerId, String document, VehicleId vehicleId, WorkOrderStatus status) {

    public WorkOrderFilter {
        document = Document.sanitize(document);
    }

    public static WorkOrderFilter of(CustomerId customerId, String document, VehicleId vehicleId, WorkOrderStatus status) {
        return new WorkOrderFilter(customerId, document, vehicleId, status);
    }

    public WorkOrderFilter withCustomerId(CustomerId customerId) {
        return of(customerId, this.document, this.vehicleId, this.status);
    }
}
