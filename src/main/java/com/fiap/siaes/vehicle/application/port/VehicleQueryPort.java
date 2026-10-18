package com.fiap.siaes.vehicle.application.port;

import com.fiap.siaes.vehicle.application.dto.VehicleFilter;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;

public interface VehicleQueryPort {

    PageResponse<VehicleResponse> findAllByFilter(VehicleFilter filter, PageQuery pageQuery);
}
