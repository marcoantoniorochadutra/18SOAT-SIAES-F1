package com.fiap.siaes.workorder.application.usecase;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.workorder.application.dto.ListWorkOrderResponse;
import com.fiap.siaes.workorder.application.dto.WorkOrderFilter;

public interface ListWorkOrdersUseCase {

    PageResponse<ListWorkOrderResponse> execute(ListWorkOrdersCommand command);

    record ListWorkOrdersCommand(WorkOrderFilter filter, PageQuery pageQuery, AuthenticatedUser authenticatedUser) {
        public static ListWorkOrdersCommand of(WorkOrderFilter filter, PageQuery pageQuery, AuthenticatedUser authenticatedUser) {
            return new ListWorkOrdersCommand(filter, pageQuery, authenticatedUser);
        }
    }
}
