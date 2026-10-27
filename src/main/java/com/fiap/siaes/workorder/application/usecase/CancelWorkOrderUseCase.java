package com.fiap.siaes.workorder.application.usecase;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

public interface CancelWorkOrderUseCase {

    void execute(CancelWorkOrderCommand command);

    record CancelWorkOrderCommand(WorkOrderId id, AuthenticatedUser authenticatedUser) {

        public static CancelWorkOrderCommand of(WorkOrderId id, AuthenticatedUser authenticatedUser) {
            return new CancelWorkOrderCommand(id, authenticatedUser);
        }
    }
}
