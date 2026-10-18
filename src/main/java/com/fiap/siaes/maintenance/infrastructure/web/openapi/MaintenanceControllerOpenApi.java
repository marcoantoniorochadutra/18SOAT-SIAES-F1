package com.fiap.siaes.maintenance.infrastructure.web.openapi;

import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.maintenance.application.dto.MaintenanceFilter;
import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;
import com.fiap.siaes.maintenance.application.usecase.CreateMaintenanceUseCase.CreateMaintenanceCommand;
import com.fiap.siaes.maintenance.application.usecase.UpdateMaintenanceUseCase.UpdateMaintenanceCommand;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(name = "Services", description = "maintenance.tag.description")
public interface MaintenanceControllerOpenApi {

    @Operation(summary = "maintenance.create.summary", description = "maintenance.create.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "201", description = "maintenance.create.response.success",
                                  headers = @Header(name = "Location")),
                     @ApiResponse(responseCode = "400", description = "maintenance.create.response.badrequest"),
                     @ApiResponse(responseCode = "403", description = "maintenance.response.forbidden"),
                     @ApiResponse(responseCode = "409", description = "maintenance.response.conflict"),
    })
    ResponseEntity<MaintenanceId> create(CreateMaintenanceCommand command);

    @Operation(summary = "maintenance.update.summary", description = "maintenance.update.description", method = "PUT",
               responses = {
                     @ApiResponse(responseCode = "200", description = "maintenance.update.response.success",
                                  headers = @Header(name = "Location")),
                     @ApiResponse(responseCode = "400", description = "maintenance.update.response.badrequest"),
                     @ApiResponse(responseCode = "403", description = "maintenance.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "maintenance.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "maintenance.response.conflict"),
    })
    ResponseEntity<MaintenanceId> update(MaintenanceId id, UpdateMaintenanceCommand command);

    @Operation(summary = "maintenance.get.summary", description = "maintenance.get.description", method = "GET",
               responses = {
                     @ApiResponse(responseCode = "200", description = "maintenance.get.response.success"),
                     @ApiResponse(responseCode = "403", description = "maintenance.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "maintenance.response.notfound"),
    })
    MaintenanceResponse getById(MaintenanceId id);

    @Operation(summary = "maintenance.list.summary", description = "maintenance.list.description", method = "GET",
               parameters = {
                       @Parameter(name = "code", in = ParameterIn.QUERY, description = "maintenance.list.parameter.code", example = "TROCA-OLEO"),
                       @Parameter(name = "description", in = ParameterIn.QUERY, description = "maintenance.list.parameter.description", example = "Troca de óleo"),
                       @Parameter(name = "active", in = ParameterIn.QUERY, description = "maintenance.list.parameter.active", example = "true"),
                       @Parameter(name = "page", in = ParameterIn.QUERY, description = "common.parameter.page", example = "0"),
                       @Parameter(name = "size", in = ParameterIn.QUERY, description = "common.parameter.size", example = "20")
               },
               responses = {
                     @ApiResponse(responseCode = "200", description = "maintenance.list.response.success"),
                     @ApiResponse(responseCode = "403", description = "maintenance.response.forbidden"),
    })
    PageResponse<MaintenanceResponse> list(@ParameterObject MaintenanceFilter filter, @ParameterObject PageQuery pageQuery);

    @Operation(summary = "maintenance.deactivate.summary", description = "maintenance.deactivate.description", method = "PATCH",
               responses = {
                     @ApiResponse(responseCode = "204", description = "maintenance.deactivate.response.success"),
                     @ApiResponse(responseCode = "403", description = "maintenance.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "maintenance.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "maintenance.deactivate.response.conflict"),
    })
    void deactivate(MaintenanceId id);

    @Operation(summary = "maintenance.activate.summary", description = "maintenance.activate.description", method = "PATCH",
               responses = {
                     @ApiResponse(responseCode = "204", description = "maintenance.activate.response.success"),
                     @ApiResponse(responseCode = "403", description = "maintenance.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "maintenance.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "maintenance.activate.response.conflict"),
    })
    void activate(MaintenanceId id);

}
