package com.fiap.siaes.quotation.infrastructure.web.openapi;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalLinkResponse;
import com.fiap.siaes.quotation.application.dto.QuotationFilter;
import com.fiap.siaes.quotation.application.dto.QuotationResponse;
import com.fiap.siaes.quotation.application.dto.ListQuotationResponse;
import com.fiap.siaes.quotation.application.usecase.CreateQuotationUseCase.CreateQuotationCommand;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByCustomerUseCase.DecideQuotationByCustomerCommand;
import com.fiap.siaes.quotation.application.usecase.UpdateQuotationUseCase.UpdateQuotationCommand;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(name = "Quotations", description = "quotation.tag.description")
public interface QuotationControllerOpenApi {

    @Operation(summary = "quotation.create.summary", description = "quotation.create.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "201", description = "quotation.create.response.success",
                                  headers = @Header(name = "Location")),
                     @ApiResponse(responseCode = "400", description = "quotation.create.response.badrequest"),
                     @ApiResponse(responseCode = "403", description = "quotation.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "quotation.create.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "quotation.create.response.conflict"),
    })
    ResponseEntity<QuotationId> create(CreateQuotationCommand command, AuthenticatedUser authenticatedUser);

    @Operation(summary = "quotation.update.summary", description = "quotation.update.description", method = "PUT",
               responses = {
                     @ApiResponse(responseCode = "200", description = "quotation.update.response.success",
                                  headers = @Header(name = "Location")),
                     @ApiResponse(responseCode = "400", description = "quotation.update.response.badrequest"),
                     @ApiResponse(responseCode = "403", description = "quotation.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "quotation.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "quotation.update.response.conflict"),
    })
    ResponseEntity<QuotationId> update(QuotationId id, UpdateQuotationCommand command,
                                       AuthenticatedUser authenticatedUser);

    @Operation(summary = "quotation.complete.summary", description = "quotation.complete.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "204", description = "quotation.complete.response.success"),
                     @ApiResponse(responseCode = "403", description = "quotation.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "quotation.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "quotation.complete.response.conflict"),
    })
    void complete(QuotationId id);

    @Operation(summary = "quotation.get.summary", description = "quotation.get.description", method = "GET",
               responses = {
                     @ApiResponse(responseCode = "200", description = "quotation.get.response.success"),
                     @ApiResponse(responseCode = "403", description = "quotation.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "quotation.response.notfound"),
    })
    QuotationResponse getById(QuotationId id, AuthenticatedUser authenticatedUser);

    @Operation(summary = "quotation.list.summary", description = "quotation.list.description", method = "GET",
               parameters = {
                       @Parameter(name = "workOrderId", in = ParameterIn.QUERY, description = "quotation.list.parameter.workOrderId"),
                       @Parameter(name = "customerId", in = ParameterIn.QUERY, description = "quotation.list.parameter.customerId"),
                       @Parameter(name = "vehicleId", in = ParameterIn.QUERY, description = "quotation.list.parameter.vehicleId"),
                       @Parameter(name = "status", in = ParameterIn.QUERY, description = "quotation.list.parameter.status", example = "AWAITING_APPROVAL"),
                       @Parameter(name = "page", in = ParameterIn.QUERY, description = "common.parameter.page", example = "0"),
                       @Parameter(name = "size", in = ParameterIn.QUERY, description = "common.parameter.size", example = "20")
               },
               responses = {
                     @ApiResponse(responseCode = "200", description = "quotation.list.response.success"),
                     @ApiResponse(responseCode = "403", description = "quotation.response.forbidden"),
    })
    PageResponse<ListQuotationResponse> list(@ParameterObject QuotationFilter filter, @ParameterObject PageQuery pageQuery, AuthenticatedUser authenticatedUser);

    @Operation(summary = "quotation.approvalLink.summary", description = "quotation.approvalLink.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "200", description = "quotation.approvalLink.response.success"),
                     @ApiResponse(responseCode = "403", description = "quotation.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "quotation.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "quotation.decision.response.conflict"),
    })
    QuotationApprovalLinkResponse getApprovalLink(QuotationId id);

    @Operation(summary = "quotation.decide.summary", description = "quotation.decide.description", method = "PATCH",
               responses = {
                     @ApiResponse(responseCode = "204", description = "quotation.decide.response.success"),
                     @ApiResponse(responseCode = "400", description = "quotation.decide.response.badrequest"),
                     @ApiResponse(responseCode = "403", description = "quotation.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "quotation.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "quotation.decision.response.conflict"),
    })
    void decide(QuotationId id, DecideQuotationByCustomerCommand command, AuthenticatedUser authenticatedUser);

}
