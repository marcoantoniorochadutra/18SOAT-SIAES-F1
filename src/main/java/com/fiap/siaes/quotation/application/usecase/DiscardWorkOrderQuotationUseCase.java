package com.fiap.siaes.quotation.application.usecase;

import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

public interface DiscardWorkOrderQuotationUseCase {

    void execute(DiscardWorkOrderQuotationCommand command);

    record DiscardWorkOrderQuotationCommand(WorkOrderId workOrderId, UserId discardedBy) {

        public static DiscardWorkOrderQuotationCommand of(WorkOrderId workOrderId, UserId discardedBy) {
            return new DiscardWorkOrderQuotationCommand(workOrderId, discardedBy);
        }
    }
}
