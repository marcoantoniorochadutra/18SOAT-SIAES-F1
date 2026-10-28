package com.fiap.siaes.workorder.application.port;

import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.workorder.application.dto.WorkOrderFilter;
import com.fiap.siaes.workorder.application.dto.ListWorkOrderResponse;
import com.fiap.siaes.workorder.domain.model.vo.StatusTimeline;

import java.time.Instant;
import java.util.List;

public interface WorkOrderQueryPort {

    PageResponse<ListWorkOrderResponse> findAllByFilter(WorkOrderFilter filter, PageQuery pageQuery);

    List<StatusTimeline> findStatusTimelinesOfFinishedBetween(Instant from, Instant to);
}
