package com.fiap.siaes.workorder.infrastructure.web.openapi;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.workorder.application.dto.WorkOrderFilter;
import com.fiap.siaes.workorder.application.dto.WorkOrderResponse;
import com.fiap.siaes.workorder.application.dto.ListWorkOrderResponse;
import com.fiap.siaes.workorder.application.usecase.CreateWorkOrderUseCase.CreateWorkOrderCommand;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(name = "Work Orders", description = "workorder.tag.description")
public interface WorkOrderControllerOpenApi {

    @Operation(summary = "workorder.create.summary", description = "workorder.create.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "201", description = "workorder.create.response.success",
                                  headers = @Header(name = "Location")),
                     @ApiResponse(responseCode = "400", description = "workorder.create.response.badrequest"),
                     @ApiResponse(responseCode = "403", description = "workorder.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "workorder.create.response.notfound"),
    })
    ResponseEntity<WorkOrderId> create(CreateWorkOrderCommand command);

    @Operation(summary = "workorder.diagnosis.summary", description = "workorder.diagnosis.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "204", description = "workorder.diagnosis.response.success"),
                     @ApiResponse(responseCode = "403", description = "workorder.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "workorder.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "workorder.diagnosis.response.conflict"),
    })
    void startDiagnosis(WorkOrderId id);

    @Operation(summary = "workorder.finish.summary", description = "workorder.finish.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "204", description = "workorder.finish.response.success"),
                     @ApiResponse(responseCode = "403", description = "workorder.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "workorder.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "workorder.finish.response.conflict"),
    })
    void finish(WorkOrderId id);

    @Operation(summary = "workorder.deliver.summary", description = "workorder.deliver.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "204", description = "workorder.deliver.response.success"),
                     @ApiResponse(responseCode = "403", description = "workorder.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "workorder.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "workorder.deliver.response.conflict"),
    })
    void deliver(WorkOrderId id);

    @Operation(summary = "workorder.cancel.summary", description = "workorder.cancel.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "204", description = "workorder.cancel.response.success"),
                     @ApiResponse(responseCode = "403", description = "workorder.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "workorder.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "workorder.cancel.response.conflict"),
    })
    void cancel(WorkOrderId id, AuthenticatedUser authenticatedUser);

    @Operation(summary = "workorder.get.summary", description = "workorder.get.description", method = "GET",
               responses = {
                     @ApiResponse(responseCode = "200", description = "workorder.get.response.success"),
                     @ApiResponse(responseCode = "403", description = "workorder.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "workorder.response.notfound"),
    })
    WorkOrderResponse getById(WorkOrderId id, AuthenticatedUser authenticatedUser);

    @Operation(summary = "workorder.list.summary", description = "workorder.list.description", method = "GET",
               parameters = {
                       @Parameter(name = "customerId", in = ParameterIn.QUERY, description = "workorder.list.parameter.customerId"),
                       @Parameter(name = "document", in = ParameterIn.QUERY, description = "workorder.list.parameter.document", example = "529.982.247-25"),
                       @Parameter(name = "vehicleId", in = ParameterIn.QUERY, description = "workorder.list.parameter.vehicleId"),
                       @Parameter(name = "status", in = ParameterIn.QUERY, description = "workorder.list.parameter.status", example = "RECEIVED"),
                       @Parameter(name = "page", in = ParameterIn.QUERY, description = "common.parameter.page", example = "0"),
                       @Parameter(name = "size", in = ParameterIn.QUERY, description = "common.parameter.size", example = "20")
               },
               responses = {
                     @ApiResponse(responseCode = "200", description = "workorder.list.response.success"),
                     @ApiResponse(responseCode = "403", description = "workorder.response.forbidden"),
    })
    PageResponse<ListWorkOrderResponse> list(@ParameterObject WorkOrderFilter filter, @ParameterObject PageQuery pageQuery, AuthenticatedUser authenticatedUser);

}
