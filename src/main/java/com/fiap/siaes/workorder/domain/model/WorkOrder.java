package com.fiap.siaes.workorder.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder {

    private WorkOrderId id;
    private CustomerId customerId;

    private WorkOrderStatus lastStatus;
    private Set<WorkOrderStatusHistory> statusHistory;

    private Set<Quotation> quotations;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant finishedAt;


}
