package com.fiap.siaes.customer.infrastructure.web;

import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.customer.application.dto.CustomerFilter;
import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase.CreateCustomerCommand;
import com.fiap.siaes.customer.application.usecase.DeleteCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.GetCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.GetCustomerUseCase.GetCustomerByIdCommand;
import com.fiap.siaes.customer.application.usecase.ListCustomersUseCase;
import com.fiap.siaes.customer.application.usecase.ListCustomersUseCase.ListCustomersCommand;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase.UpdateCustomerCommand;
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

@RestController
@RequiredArgsConstructor
@RequestMapping(CustomerController.ENDPOINT)
@Authentication(minimumRole = UserRole.MANAGER)
public class CustomerController implements CustomerControllerOpenApi {

    public static final String ENDPOINT = "/api/v1/customers";

    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
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
    public CustomerResponse update(@PathVariable CustomerId id,
                                   @Valid @RequestBody UpdateCustomerCommand command,
                                   @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return this.updateCustomerUseCase.execute(command.with(id, authenticatedUser));
    }

    @Override
    @GetMapping("/{id}")
    @Authentication(minimumRole = UserRole.CUSTOMER)
    public CustomerResponse getById(@PathVariable CustomerId id,
                                    @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return this.getCustomerUseCase.execute(GetCustomerByIdCommand.from(id, authenticatedUser));
    }

    @Override
    @GetMapping
    public PageResponse<CustomerResponse> listAllCustomer(@ParameterObject CustomerFilter filter,
                                                          @Valid @ParameterObject PageQuery pageQuery) {
        return this.listCustomersUseCase.execute(ListCustomersCommand.from(filter, pageQuery));
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable CustomerId id) {
        this.deleteCustomerUseCase.execute(id);
    }
}
