package com.fiap.siaes.workorder.application.port;

import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

public interface WorkOrderQuotationPort {

    boolean hasQuotationAwaitingApproval(WorkOrderId workOrderId);
}
