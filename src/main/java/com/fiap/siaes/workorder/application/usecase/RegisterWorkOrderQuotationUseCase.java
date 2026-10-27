package com.fiap.siaes.workorder.application.usecase;

import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

public interface RegisterWorkOrderQuotationUseCase {

    void execute(RegisterWorkOrderQuotationCommand command);

    record RegisterWorkOrderQuotationCommand(WorkOrderId workOrderId) {

        public static RegisterWorkOrderQuotationCommand of(WorkOrderId workOrderId) {
            return new RegisterWorkOrderQuotationCommand(workOrderId);
        }
    }
}
