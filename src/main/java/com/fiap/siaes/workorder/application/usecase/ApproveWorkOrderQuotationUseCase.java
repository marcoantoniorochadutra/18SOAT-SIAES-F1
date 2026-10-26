package com.fiap.siaes.workorder.application.usecase;

import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

public interface ApproveWorkOrderQuotationUseCase {

    void execute(ApproveWorkOrderQuotationCommand command);

    record ApproveWorkOrderQuotationCommand(WorkOrderId workOrderId) {

        public static ApproveWorkOrderQuotationCommand of(WorkOrderId workOrderId) {
            return new ApproveWorkOrderQuotationCommand(workOrderId);
        }
    }
}
