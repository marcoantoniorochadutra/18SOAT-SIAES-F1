package com.fiap.siaes.supplies.infrastructure.web.openapi;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.application.dto.SuppliesFilter;
import com.fiap.siaes.supplies.application.dto.SuppliesResponse;
import com.fiap.siaes.supplies.application.usecase.CreateSuppliesUseCase.CreateSuppliesCommand;
import com.fiap.siaes.supplies.application.usecase.UpdateSuppliesUseCase.UpdateSuppliesCommand;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(name = "Supplies", description = "supplies.tag.description")
public interface SuppliesControllerOpenApi {

    @Operation(summary = "supplies.create.summary", description = "supplies.create.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "201", description = "supplies.create.response.success",
                                  headers = @Header(name = "Location")),
                     @ApiResponse(responseCode = "400", description = "supplies.create.response.badrequest"),
                     @ApiResponse(responseCode = "403", description = "supplies.response.forbidden"),
                     @ApiResponse(responseCode = "409", description = "supplies.response.conflict"),
    })
    ResponseEntity<SuppliesId> create(CreateSuppliesCommand command, AuthenticatedUser authenticatedUser);

    @Operation(summary = "supplies.update.summary", description = "supplies.update.description", method = "PUT",
               responses = {
                     @ApiResponse(responseCode = "200", description = "supplies.update.response.success",
                                  headers = @Header(name = "Location")),
                     @ApiResponse(responseCode = "400", description = "supplies.update.response.badrequest"),
                     @ApiResponse(responseCode = "403", description = "supplies.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "supplies.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "supplies.response.conflict"),
    })
    ResponseEntity<SuppliesId> update(SuppliesId id, UpdateSuppliesCommand command);

    @Operation(summary = "supplies.get.summary", description = "supplies.get.description", method = "GET",
               responses = {
                     @ApiResponse(responseCode = "200", description = "supplies.get.response.success"),
                     @ApiResponse(responseCode = "403", description = "supplies.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "supplies.response.notfound"),
    })
    SuppliesResponse getById(SuppliesId id);

    @Operation(summary = "supplies.list.summary", description = "supplies.list.description", method = "GET",
               parameters = {
                       @Parameter(name = "sku", in = ParameterIn.QUERY, description = "supplies.list.parameter.sku", example = "FLT-OLEO-001"),
                       @Parameter(name = "description", in = ParameterIn.QUERY, description = "supplies.list.parameter.description", example = "Filtro de óleo"),
                       @Parameter(name = "active", in = ParameterIn.QUERY, description = "supplies.list.parameter.active", example = "true"),
                       @Parameter(name = "page", in = ParameterIn.QUERY, description = "common.parameter.page", example = "0"),
                       @Parameter(name = "size", in = ParameterIn.QUERY, description = "common.parameter.size", example = "20")
               },
               responses = {
                     @ApiResponse(responseCode = "200", description = "supplies.list.response.success"),
                     @ApiResponse(responseCode = "403", description = "supplies.response.forbidden"),
    })
    PageResponse<SuppliesResponse> list(@ParameterObject SuppliesFilter filter, @ParameterObject PageQuery pageQuery);

    @Operation(summary = "supplies.deactivate.summary", description = "supplies.deactivate.description", method = "PATCH",
               responses = {
                     @ApiResponse(responseCode = "204", description = "supplies.deactivate.response.success"),
                     @ApiResponse(responseCode = "403", description = "supplies.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "supplies.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "supplies.deactivate.response.conflict"),
    })
    void deactivate(SuppliesId id);

    @Operation(summary = "supplies.activate.summary", description = "supplies.activate.description", method = "PATCH",
               responses = {
                     @ApiResponse(responseCode = "204", description = "supplies.activate.response.success"),
                     @ApiResponse(responseCode = "403", description = "supplies.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "supplies.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "supplies.activate.response.conflict"),
    })
    void activate(SuppliesId id);

}
