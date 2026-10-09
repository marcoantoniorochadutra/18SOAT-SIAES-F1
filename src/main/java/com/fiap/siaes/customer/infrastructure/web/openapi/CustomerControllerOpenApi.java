package com.fiap.siaes.customer.infrastructure.web.openapi;

import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.customer.application.dto.CustomerFilter;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase.CreateCustomerCommand;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase.UpdateCustomerCommand;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
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

@Tag(name = "Customers", description = "customer.tag.description")
public interface CustomerControllerOpenApi {

    @Operation(summary = "customer.create.summary", description = "customer.create.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "201", description = "customer.create.response.success",
                                  headers = @Header(name = "Location")),
                     @ApiResponse(responseCode = "400", description = "customer.create.response.badrequest"),
    })
    public ResponseEntity<CustomerId> create(CreateCustomerCommand command);

    @Operation(summary = "customer.update.summary", description = "customer.update.description", method = "PUT",
               responses = {
                     @ApiResponse(responseCode = "200", description = "customer.update.response.success"),
                     @ApiResponse(responseCode = "400", description = "customer.update.response.badrequest"),
                     @ApiResponse(responseCode = "404", description = "customer.update.response.notfound"),
    })
    public CustomerResponse update(CustomerId id, UpdateCustomerCommand command, AuthenticatedUser authenticatedUser);

    @Operation(summary = "customer.get.summary", description = "customer.get.description", method = "GET",
               responses = {
                     @ApiResponse(responseCode = "200", description = "customer.get.response.success"),
                     @ApiResponse(responseCode = "404", description = "customer.get.response.notfound"),
    })
    public CustomerResponse getById(CustomerId id, AuthenticatedUser authenticatedUser);

    @Operation(summary = "customer.list.summary", description = "customer.list.description", method = "GET",
               parameters = {
                       @Parameter(name = "name", in = ParameterIn.QUERY, description = "customer.list.parameter.name", example = "Silva"),
                       @Parameter(name = "document", in = ParameterIn.QUERY, description = "customer.list.parameter.document", example = "529.982.247-25"),
                       @Parameter(name = "email", in = ParameterIn.QUERY, description = "customer.list.parameter.email", example = "silva@email.com"),
                       @Parameter(name = "phone", in = ParameterIn.QUERY, description = "customer.list.parameter.phone", example = "98888"),
                       @Parameter(name = "page", in = ParameterIn.QUERY, description = "common.parameter.page", example = "0"),
                       @Parameter(name = "size", in = ParameterIn.QUERY, description = "common.parameter.size", example = "20")
               },
               responses = {
                     @ApiResponse(responseCode = "200", description = "customer.list.response.success"),
    })
    public PageResponse<CustomerResponse> listAllCustomer(@ParameterObject CustomerFilter filter,
                                                          @ParameterObject PageQuery pageQuery);

    @Operation(summary = "customer.delete.summary", description = "customer.delete.description", method = "DELETE",
               responses = {
                     @ApiResponse(responseCode = "204", description = "customer.delete.response.success"),
                     @ApiResponse(responseCode = "404", description = "customer.delete.response.notfound"),
    })
    public void delete(CustomerId id);

}
