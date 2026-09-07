package com.fiap.siaes.customer.infrastructure.web;

import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase.CreateCustomerCommand;
import com.fiap.siaes.customer.application.usecase.DeleteCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.GetCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.ListCustomersUseCase;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase.UpdateCustomerCommand;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.CustomerId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.fiap.siaes.sk.util.ControllerUtils.createdResponse;

@Tag(name = "Customers", description = "Gestão de clientes")
@RestController
@RequestMapping(CustomerController.ENDPOINT)
@RequiredArgsConstructor
public class CustomerController {

    public static final String ENDPOINT = "/api/v1/customers";

    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;

    @Operation(summary = "Cria um novo cliente")
    @ApiResponse(responseCode = "201", description = "Cliente criado")
    @PostMapping
    public ResponseEntity<CustomerId> create(@RequestBody CreateCustomerCommand command) {
        CustomerId id = this.createCustomerUseCase.execute(command);
        return createdResponse(ENDPOINT, id);
    }

    @Operation(summary = "Atualiza um cliente existente")
    @PutMapping("/{id}")
    public Customer update(@PathVariable CustomerId id, @RequestBody UpdateCustomerCommand command) {
        return this.updateCustomerUseCase.execute(id, command);
    }

    @Operation(summary = "Busca um cliente pelo id")
    @GetMapping("/{id}")
    public Customer get(@PathVariable CustomerId id) {
        return this.getCustomerUseCase.execute(id);
    }

    @Operation(summary = "Lista todos os clientes")
    @GetMapping
    public List<Customer> list() {
        return this.listCustomersUseCase.execute();
    }

    @Operation(summary = "Remove um cliente")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable CustomerId id) {
        this.deleteCustomerUseCase.execute(id);
    }
}
