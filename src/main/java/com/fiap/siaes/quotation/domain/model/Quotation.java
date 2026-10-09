package com.fiap.siaes.quotation.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {

    private QuotationId id;
    private WorkOrderId workOrderId;
    private String description;

    private CustomerId ownerId;
    private VehicleId vehicleId;
    private long odometer;

    private QuotationStatus lastStatus;
    private Set<QuotationStatusHistory> statusHistory;

    private List<QuotationSupplies> supplies;
    private List<QuotationMaintenance> maintenance;

}
