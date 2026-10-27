package com.fiap.siaes.workorder.application.usecase;

import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

public interface FinishWorkOrderUseCase {

    void execute(FinishWorkOrderCommand command);

    record FinishWorkOrderCommand(WorkOrderId id) {

        public static FinishWorkOrderCommand of(WorkOrderId id) {
            return new FinishWorkOrderCommand(id);
        }
    }
}
