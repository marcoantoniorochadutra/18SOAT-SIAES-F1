package com.fiap.siaes.maintenance.application.usecase;

import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.maintenance.application.dto.MaintenanceFilter;
import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;

public interface ListMaintenancesUseCase {

    PageResponse<MaintenanceResponse> execute(ListMaintenancesCommand command);

    record ListMaintenancesCommand(MaintenanceFilter filter, PageQuery pageQuery) {
        public static ListMaintenancesCommand of(MaintenanceFilter filter, PageQuery pageQuery) {
            return new ListMaintenancesCommand(filter, pageQuery);
        }
    }
}
