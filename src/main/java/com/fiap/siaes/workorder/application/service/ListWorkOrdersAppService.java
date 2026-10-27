package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.workorder.application.dto.ListWorkOrderResponse;
import com.fiap.siaes.workorder.application.dto.WorkOrderFilter;
import com.fiap.siaes.workorder.application.port.WorkOrderQueryPort;
import com.fiap.siaes.workorder.application.usecase.ListWorkOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListWorkOrdersAppService implements ListWorkOrdersUseCase {

    private final WorkOrderQueryPort workOrderQueryPort;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public PageResponse<ListWorkOrderResponse> execute(ListWorkOrdersCommand command) {
        AuthenticatedUser authenticatedUser = command.authenticatedUser();
        WorkOrderFilter filter = authenticatedUser.isCustomer() ? command.filter().withCustomerId(authenticatedUser.customerId()) : command.filter();

        return this.workOrderQueryPort.findAllByFilter(filter, command.pageQuery());
    }
}
