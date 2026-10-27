package com.fiap.siaes.workorder.application.usecase;

import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

public interface DeliverWorkOrderUseCase {

    void execute(DeliverWorkOrderCommand command);

    record DeliverWorkOrderCommand(WorkOrderId id) {

        public static DeliverWorkOrderCommand of(WorkOrderId id) {
            return new DeliverWorkOrderCommand(id);
        }
    }
}
