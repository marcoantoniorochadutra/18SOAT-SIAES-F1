package com.fiap.siaes.workorder.application.usecase;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.workorder.application.dto.WorkOrderResponse;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

public interface GetWorkOrderByIdUseCase {

    WorkOrderResponse execute(GetWorkOrderByIdCommand command);

    record GetWorkOrderByIdCommand(WorkOrderId id, AuthenticatedUser authenticatedUser) {
        public static GetWorkOrderByIdCommand of(WorkOrderId id, AuthenticatedUser authenticatedUser) {
            return new GetWorkOrderByIdCommand(id, authenticatedUser);
        }
    }
}
