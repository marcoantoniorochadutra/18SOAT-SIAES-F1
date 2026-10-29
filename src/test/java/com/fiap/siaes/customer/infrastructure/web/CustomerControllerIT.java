package com.fiap.siaes.customer.infrastructure.web;

import com.fiap.siaes.customer.CustomerTestFactory;
import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase.CreateCustomerCommand;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase.UpdateCustomerCommand;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.utils.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.utils.TestFactory.persist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Test - Customer")
class CustomerControllerIT extends IntegrationTestUtils {

    private static final String DOCUMENT = "529.982.247-25";

    private String managerToken;

    @BeforeEach
    void setUp() {
        this.managerToken = super.loginAs(UserRole.MANAGER);
    }

    @Test
    @DisplayName("[201] Deve criar cliente")
    void shouldCreateCustomer() throws Exception {
        var command = CreateCustomerCommand.of(DOCUMENT, "Maria Silva", "11999999999", "maria@email.com");

        var id = super.createdId(super.executePost(CustomerController.ENDPOINT, this.managerToken, command));

        var customer = super.customerRepository.findByIdOrThrowNotFound(new CustomerId(id));
        assertEquals("Maria Silva", customer.getName());
        assertEquals("maria@email.com", customer.getEmail());
    }

    @Test
    @DisplayName("[200] Deve buscar cliente")
    void shouldGetCustomer() throws Exception {
        var customer = this.persistCustomer();

        super.executeGet(this.url(customer), this.managerToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.id", customer.getId().toString()))
                .andExpect(jsonValue("$.name", customer.getName()))
                .andExpect(jsonValue("$.email", customer.getEmail()));
    }

    @Test
    @DisplayName("[409] Não deve criar cliente com e-mail já cadastrado")
    void shouldNotCreateCustomerWithDuplicatedEmail() throws Exception {
        var customer = this.persistCustomer();

        super.executePost(CustomerController.ENDPOINT, this.managerToken,
                          CreateCustomerCommand.of(DOCUMENT, "João Souza", null, customer.getEmail()))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("[200] Deve atualizar cliente")
    void shouldUpdateCustomer() throws Exception {
        var customer = this.persistCustomer();
        var command = UpdateCustomerCommand.builder()
                .document(DOCUMENT)
                .name("Maria Souza")
                .email(customer.getEmail())
                .build();

        super.executePut(this.url(customer), this.managerToken, command)
                .andExpect(status().isOk());

        assertEquals("Maria Souza", super.customerRepository.findByIdOrThrowNotFound(customer.getId()).getName());
    }

    @Test
    @DisplayName("[200] Deve listar clientes")
    void shouldListCustomers() throws Exception {
        var customer = this.persistCustomer();

        super.executeGet(CustomerController.ENDPOINT, this.managerToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.content.length()", 1))
                .andExpect(jsonValue("$.content[0].id", customer.getId().toString()));
    }

    @Test
    @DisplayName("[204] Deve excluir cliente")
    void shouldDeleteCustomer() throws Exception {
        var customer = this.persistCustomer();

        super.executeDelete(this.url(customer), this.managerToken)
                .andExpect(status().isNoContent());

        assertTrue(super.customerRepository.findById(customer.getId()).isEmpty());
    }

    @Test
    @DisplayName("[200] Cliente deve buscar o próprio cadastro")
    void shouldGetOwnCustomerAsCustomer() throws Exception {
        var customer = this.persistCustomer();
        var customerToken = super.loginAs(UserRole.CUSTOMER, customer.getId());

        super.executeGet(this.url(customer), customerToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.id", customer.getId().toString()));
    }

    @Test
    @DisplayName("[403] Cliente não deve listar clientes")
    void shouldNotListCustomersAsCustomer() throws Exception {
        var customer = this.persistCustomer();
        var customerToken = super.loginAs(UserRole.CUSTOMER, customer.getId());

        super.executeGet(CustomerController.ENDPOINT, customerToken)
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[401] Não deve listar clientes sem token")
    void shouldNotListCustomersWithoutToken() throws Exception {
        super.executeGet(CustomerController.ENDPOINT, null)
                .andExpect(status().isUnauthorized());
    }

    private Customer persistCustomer() {
        return persist(super.customerRepository, CustomerTestFactory.createCustomer());
    }

    private String url(Customer customer) {
        return CustomerController.ENDPOINT + "/" + customer.getId();
    }
}
