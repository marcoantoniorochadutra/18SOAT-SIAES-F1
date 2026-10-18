package com.fiap.siaes.maintenance.application.port;

import com.fiap.siaes.maintenance.application.dto.MaintenanceFilter;
import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;

public interface MaintenanceQueryPort {

    PageResponse<MaintenanceResponse> findAllByFilter(MaintenanceFilter filter, PageQuery pageQuery);
}
