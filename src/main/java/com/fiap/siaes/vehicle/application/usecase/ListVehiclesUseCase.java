package com.fiap.siaes.vehicle.application.usecase;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.vehicle.application.dto.VehicleFilter;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;

public interface ListVehiclesUseCase {

    PageResponse<VehicleResponse> execute(ListVehiclesCommand command);

    record ListVehiclesCommand(VehicleFilter filter, PageQuery pageQuery, AuthenticatedUser authenticatedUser) {
        public static ListVehiclesCommand of(VehicleFilter filter, PageQuery pageQuery, AuthenticatedUser authenticatedUser) {
            return new ListVehiclesCommand(filter, pageQuery, authenticatedUser);
        }
    }
}
