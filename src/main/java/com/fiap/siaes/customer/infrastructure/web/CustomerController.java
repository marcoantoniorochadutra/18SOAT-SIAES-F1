package com.fiap.siaes.customer.infrastructure.web;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.customer.application.dto.CustomerFilter;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase.CreateCustomerCommand;
import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.DeleteCustomerUseCase.DeleteCustomerCommand;
import com.fiap.siaes.customer.application.usecase.DeleteCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.GetCustomerByIdUseCase.GetCustomerByIdCommand;
import com.fiap.siaes.customer.application.usecase.GetCustomerByIdUseCase;
import com.fiap.siaes.customer.application.usecase.ListCustomersUseCase.ListCustomersCommand;
import com.fiap.siaes.customer.application.usecase.ListCustomersUseCase;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase.UpdateCustomerCommand;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.infrastructure.web.openapi.CustomerControllerOpenApi;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.fiap.siaes.sk.util.ControllerUtils.createdResponse;
import static com.fiap.siaes.sk.util.ControllerUtils.updatedResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(CustomerController.ENDPOINT)
@Authentication(minimumRole = UserRole.MANAGER)
public class CustomerController implements CustomerControllerOpenApi {

    public static final String ENDPOINT = "/api/v1/customers";

    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final GetCustomerByIdUseCase getCustomerByIdUseCase;
    private final ListCustomersUseCase listCustomersUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;

    @Override
    @PostMapping
    public ResponseEntity<CustomerId> create(@Valid @RequestBody CreateCustomerCommand command) {
        CustomerId id = this.createCustomerUseCase.execute(command);
        return createdResponse(ENDPOINT, id);
    }

    @Override
    @PutMapping("/{id}")
    @Authentication(minimumRole = UserRole.CUSTOMER)
    public ResponseEntity<CustomerId> update(@PathVariable CustomerId id,
                                             @Valid @RequestBody UpdateCustomerCommand command,
                                             @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        CustomerId updatedId = this.updateCustomerUseCase.execute(command.with(id, authenticatedUser));
        return updatedResponse(ENDPOINT, updatedId);
    }

    @Override
    @GetMapping("/{id}")
    @Authentication(minimumRole = UserRole.CUSTOMER)
    public CustomerResponse getById(@PathVariable CustomerId id,
                                    @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return this.getCustomerByIdUseCase.execute(GetCustomerByIdCommand.of(id, authenticatedUser));
    }

    @Override
    @GetMapping
    public PageResponse<CustomerResponse> listAllCustomer(@ParameterObject CustomerFilter filter,
                                                          @Valid @ParameterObject PageQuery pageQuery) {
        return this.listCustomersUseCase.execute(ListCustomersCommand.of(filter, pageQuery));
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable CustomerId id, @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        this.deleteCustomerUseCase.execute(DeleteCustomerCommand.of(id, authenticatedUser));
    }
}
