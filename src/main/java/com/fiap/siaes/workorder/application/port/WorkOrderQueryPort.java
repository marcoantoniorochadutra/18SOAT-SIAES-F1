package com.fiap.siaes.workorder.application.port;

import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.workorder.application.dto.WorkOrderFilter;
import com.fiap.siaes.workorder.application.dto.ListWorkOrderResponse;

public interface WorkOrderQueryPort {

    PageResponse<ListWorkOrderResponse> findAllByFilter(WorkOrderFilter filter, PageQuery pageQuery);
}
