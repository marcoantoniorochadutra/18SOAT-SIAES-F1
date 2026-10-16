package com.fiap.siaes.vehicle.infrastructure.web.openapi;

import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.vehicle.application.dto.VehicleFilter;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.vehicle.application.usecase.CreateVehicleUseCase.CreateVehicleCommand;
import com.fiap.siaes.vehicle.application.usecase.UpdateVehicleUseCase.UpdateVehicleCommand;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(name = "Vehicles", description = "vehicle.tag.description")
public interface VehicleControllerOpenApi {

    @Operation(summary = "vehicle.create.summary", description = "vehicle.create.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "201", description = "vehicle.create.response.success",
                                  headers = @Header(name = "Location")),
                     @ApiResponse(responseCode = "400", description = "vehicle.create.response.badrequest"),
                     @ApiResponse(responseCode = "403", description = "vehicle.create.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "vehicle.create.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "vehicle.response.conflict"),
    })
    ResponseEntity<VehicleId> create(CreateVehicleCommand command, AuthenticatedUser authenticatedUser);

    @Operation(summary = "vehicle.update.summary", description = "vehicle.update.description", method = "PUT",
               responses = {
                     @ApiResponse(responseCode = "200", description = "vehicle.update.response.success",
                                  headers = @Header(name = "Location")),
                     @ApiResponse(responseCode = "400", description = "vehicle.update.response.badrequest"),
                     @ApiResponse(responseCode = "404", description = "vehicle.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "vehicle.response.conflict"),
    })
    ResponseEntity<VehicleId> update(VehicleId id, UpdateVehicleCommand command, AuthenticatedUser authenticatedUser);

    @Operation(summary = "vehicle.get.summary", description = "vehicle.get.description", method = "GET",
               responses = {
                     @ApiResponse(responseCode = "200", description = "vehicle.get.response.success"),
                     @ApiResponse(responseCode = "404", description = "vehicle.response.notfound"),
    })
    VehicleResponse getById(VehicleId id, AuthenticatedUser authenticatedUser);

    @Operation(summary = "vehicle.list.summary", description = "vehicle.list.description", method = "GET",
               parameters = {
                       @Parameter(name = "plate", in = ParameterIn.QUERY, description = "vehicle.list.parameter.plate", example = "ABC1234"),
                       @Parameter(name = "brand", in = ParameterIn.QUERY, description = "vehicle.list.parameter.brand", example = "Fiat"),
                       @Parameter(name = "model", in = ParameterIn.QUERY, description = "vehicle.list.parameter.model", example = "Uno"),
                       @Parameter(name = "year", in = ParameterIn.QUERY, description = "vehicle.list.parameter.year", example = "2020"),
                       @Parameter(name = "customerId", in = ParameterIn.QUERY, description = "vehicle.list.parameter.customerId"),
                       @Parameter(name = "page", in = ParameterIn.QUERY, description = "common.parameter.page", example = "0"),
                       @Parameter(name = "size", in = ParameterIn.QUERY, description = "common.parameter.size", example = "20")
               },
               responses = {
                     @ApiResponse(responseCode = "200", description = "vehicle.list.response.success"),
    })
    PageResponse<VehicleResponse> list(@ParameterObject VehicleFilter filter, @ParameterObject PageQuery pageQuery);

    @Operation(summary = "vehicle.delete.summary", description = "vehicle.delete.description", method = "DELETE",
               responses = {
                     @ApiResponse(responseCode = "204", description = "vehicle.delete.response.success"),
                     @ApiResponse(responseCode = "404", description = "vehicle.response.notfound"),
    })
    void delete(VehicleId id, AuthenticatedUser authenticatedUser);

}
