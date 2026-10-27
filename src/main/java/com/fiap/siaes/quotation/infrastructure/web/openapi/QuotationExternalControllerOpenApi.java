package com.fiap.siaes.quotation.infrastructure.web.openapi;

import com.fiap.siaes.quotation.application.dto.QuotationApprovalResponse;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationByTokenUseCase.DecideQuotationByTokenCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Quotation Approvals", description = "quotationApproval.tag.description")
public interface QuotationExternalControllerOpenApi {

    @Operation(summary = "quotationApproval.get.summary", description = "quotationApproval.get.description", method = "GET",
               parameters = @Parameter(name = "token", in = ParameterIn.PATH, description = "quotationApproval.parameter.token"),
               responses = {
                     @ApiResponse(responseCode = "200", description = "quotationApproval.get.response.success"),
                     @ApiResponse(responseCode = "404", description = "quotationApproval.response.notfound"),
    })
    QuotationApprovalResponse getByToken(String token);

    @Operation(summary = "quotationApproval.decide.summary", description = "quotationApproval.decide.description", method = "POST",
               parameters = @Parameter(name = "token", in = ParameterIn.PATH, description = "quotationApproval.parameter.token"),
               responses = {
                     @ApiResponse(responseCode = "204", description = "quotationApproval.decide.response.success"),
                     @ApiResponse(responseCode = "400", description = "quotationApproval.response.badrequest"),
                     @ApiResponse(responseCode = "404", description = "quotationApproval.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "quotationApproval.response.conflict"),
    })
    void decide(String token, DecideQuotationByTokenCommand command);
}
