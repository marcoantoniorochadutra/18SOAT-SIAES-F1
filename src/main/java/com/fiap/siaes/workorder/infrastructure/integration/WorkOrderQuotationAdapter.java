package com.fiap.siaes.workorder.infrastructure.integration;

import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.workorder.application.port.WorkOrderQuotationPort;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkOrderQuotationAdapter implements WorkOrderQuotationPort {

    private final QuotationRepository quotationRepository;

    @Override
    public boolean hasQuotationAwaitingApproval(WorkOrderId workOrderId) {
        return this.quotationRepository.existsAwaitingApprovalByWorkOrderId(workOrderId);
    }
}
