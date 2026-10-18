package com.fiap.siaes.inventory.infrastructure.web.openapi;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.inventory.application.dto.StockItemResponse;
import com.fiap.siaes.inventory.application.dto.StockMovementResponse;
import com.fiap.siaes.inventory.application.usecase.MoveStockUseCase.MoveStockCommand;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(name = "Stock", description = "stock.tag.description")
public interface StockControllerOpenApi {

    @Operation(summary = "stock.get.summary", description = "stock.get.description", method = "GET",
               responses = {
                     @ApiResponse(responseCode = "200", description = "stock.get.response.success"),
                     @ApiResponse(responseCode = "403", description = "stock.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "stock.response.notfound"),
    })
    StockItemResponse get(SuppliesId suppliesId);

    @Operation(summary = "stock.move.summary", description = "stock.move.description", method = "PATCH",
               responses = {
                     @ApiResponse(responseCode = "200", description = "stock.move.response.success",
                                  headers = @Header(name = "Location")),
                     @ApiResponse(responseCode = "400", description = "stock.move.response.badrequest"),
                     @ApiResponse(responseCode = "403", description = "stock.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "stock.response.notfound"),
                     @ApiResponse(responseCode = "409", description = "stock.move.response.conflict"),
    })
    ResponseEntity<SuppliesId> move(SuppliesId suppliesId, MoveStockCommand command, AuthenticatedUser authenticatedUser);

    @Operation(summary = "stock.movements.summary", description = "stock.movements.description", method = "GET",
               parameters = {
                       @Parameter(name = "page", in = ParameterIn.QUERY, description = "common.parameter.page", example = "0"),
                       @Parameter(name = "size", in = ParameterIn.QUERY, description = "common.parameter.size", example = "20")
               },
               responses = {
                     @ApiResponse(responseCode = "200", description = "stock.movements.response.success"),
                     @ApiResponse(responseCode = "403", description = "stock.response.forbidden"),
                     @ApiResponse(responseCode = "404", description = "stock.response.notfound"),
    })
    PageResponse<StockMovementResponse> listMovements(SuppliesId suppliesId, @ParameterObject PageQuery pageQuery);
}
