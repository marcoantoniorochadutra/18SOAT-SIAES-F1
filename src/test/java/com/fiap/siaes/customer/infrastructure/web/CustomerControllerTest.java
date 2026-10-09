package com.fiap.siaes.customer.infrastructure.web;

import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase.CreateCustomerCommand;
import com.fiap.siaes.customer.application.usecase.DeleteCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.GetCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.GetCustomerUseCase.GetCustomerByIdCommand;
import com.fiap.siaes.customer.application.usecase.ListCustomersUseCase;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase;
import com.fiap.siaes.customer.application.usecase.UpdateCustomerUseCase.UpdateCustomerCommand;
import com.fiap.siaes.customer.domain.exception.CustomerEmailAlreadyExistsException;
import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.document.domain.enums.DocumentType;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.utils.ControllerTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Controller Test - Customer")
class CustomerControllerTest extends ControllerTestUtils {

    private static final String DOCUMENT = "529.982.247-25";
    private static final String SANITIZED_DOCUMENT = "52998224725";
    private static final String EMAIL = "maria@email.com";

    @MockitoBean
    private CreateCustomerUseCase createCustomerUseCase;

    @MockitoBean
    private UpdateCustomerUseCase updateCustomerUseCase;

    @MockitoBean
    private GetCustomerUseCase getCustomerUseCase;

    @MockitoBean
    private ListCustomersUseCase listCustomersUseCase;

    @MockitoBean
    private DeleteCustomerUseCase deleteCustomerUseCase;

    @Test
    @DisplayName("[201] Deve criar cliente com sucesso")
    void shouldCreateCustomer() throws Exception {
        var command = CreateCustomerCommand.builder()
                .document(DOCUMENT)
                .name("Maria Silva")
                .phone("11999999999")
                .email(EMAIL)
                .build();

        var customerId = CustomerId.generate();

        when(this.createCustomerUseCase.execute(command)).thenReturn(customerId);

        super.executePost(CustomerController.ENDPOINT, command)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(customerId.toString())));

        verify(this.createCustomerUseCase).execute(command);
    }

    @Test
    @DisplayName("[400] Não deve criar cliente com documento em branco")
    void shouldNotCreateCustomerWhenDocumentIsBlank() throws Exception {
        var command = CreateCustomerCommand.builder()
                .document(" ")
                .name("Maria Silva")
                .email(EMAIL)
                .build();

        super.executePost(CustomerController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("CustomerCommand.document.notBlank"));
    }

    @Test
    @DisplayName("[400] Não deve criar cliente com nome em branco")
    void shouldNotCreateCustomerWhenNameIsBlank() throws Exception {
        var command = CreateCustomerCommand.builder()
                .document(DOCUMENT)
                .name(" ")
                .email(EMAIL)
                .build();

        super.executePost(CustomerController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("CustomerCommand.name.notBlank"));
    }

    @Test
    @DisplayName("[400] Não deve criar cliente com e-mail inválido")
    void shouldNotCreateCustomerWhenEmailIsInvalid() throws Exception {
        var command = CreateCustomerCommand.builder()
                .document(DOCUMENT)
                .name("Maria Silva")
                .email("invalid-email")
                .build();

        super.executePost(CustomerController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("CustomerCommand.email.email"));
    }

    @Test
    @DisplayName("[409] Não deve criar cliente com e-mail já cadastrado")
    void shouldNotCreateCustomerWhenEmailAlreadyExists() throws Exception {
        var command = CreateCustomerCommand.builder()
                .document(DOCUMENT)
                .name("Maria Silva")
                .email(EMAIL)
                .build();

        when(this.createCustomerUseCase.execute(command)).thenThrow(new CustomerEmailAlreadyExistsException(EMAIL));
        super.executePost(CustomerController.ENDPOINT, command)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("E-mail já cadastrado"));
    }

    @Test
    @DisplayName("[200] Deve atualizar cliente com sucesso")
    void shouldUpdateCustomer() throws Exception {
        var id = CustomerId.generate();
        var command = UpdateCustomerCommand.builder()
                .id(id)
                .document(DOCUMENT)
                .name("Maria Silva")
                .email(EMAIL)
                .authenticatedUser(AUTHENTICATED_USER_ADMIN)
                .build();
        var response = new CustomerResponse(id, SANITIZED_DOCUMENT, DocumentType.CPF, "Maria Silva", null, EMAIL);

        when(this.updateCustomerUseCase.execute(command)).thenReturn(response);

        super.executePutAuthenticated(CustomerController.ENDPOINT + "/" + id, command)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Maria Silva"));

        verify(this.updateCustomerUseCase).execute(command);
    }

    @Test
    @DisplayName("[400] Não deve atualizar cliente com documento em branco")
    void shouldNotUpdateCustomerWhenDocumentIsBlank() throws Exception {
        var id = CustomerId.generate();
        var command = UpdateCustomerCommand.builder()
                .document(" ")
                .name("Maria Silva")
                .email(EMAIL)
                .build();

        this.executePut(CustomerController.ENDPOINT + "/" + id, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("CustomerCommand.document.notBlank"));
    }
    @Test
    @DisplayName("[404] Não deve atualizar cliente inexistente")
    void shouldNotUpdateNonExistentCustomer() throws Exception {
        var id = CustomerId.generate();
        var command = UpdateCustomerCommand.builder()
                .id(id)
                .document(DOCUMENT)
                .name("Maria Silva")
                .email(EMAIL)
                .authenticatedUser(AUTHENTICATED_USER_ADMIN)
                .build();

        when(this.updateCustomerUseCase.execute(command))
                .thenThrow(new CustomerNotFoundException(id.toString()));

        super.executePutAuthenticated(CustomerController.ENDPOINT + "/" + id, command)
                .andExpect(status().isNotFound());

        verify(this.updateCustomerUseCase).execute(command);
    }

    @Test
    @DisplayName("[200] Deve buscar cliente pelo id com sucesso")
    void shouldGetCustomerById() throws Exception {
        var id = CustomerId.generate();
        var response = new CustomerResponse(id, SANITIZED_DOCUMENT, DocumentType.CPF, "Maria Silva", "11999999999", EMAIL);

        when(this.getCustomerUseCase.execute(GetCustomerByIdCommand.from(id, AUTHENTICATED_USER_ADMIN))).thenReturn(response);

        super.executeGetAuthenticated(CustomerController.ENDPOINT + "/" + id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    @DisplayName("[404] Não deve buscar cliente inexistente")
    void shouldNotGetNonExistentCustomer() throws Exception {
        var id = CustomerId.generate();

        when(this.getCustomerUseCase.execute(GetCustomerByIdCommand.from(id, AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new CustomerNotFoundException(id.toString()));

        super.executeGetAuthenticated(CustomerController.ENDPOINT + "/" + id)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Cliente não encontrado"));
    }

    @Test
    @DisplayName("[200] Deve listar clientes com sucesso")
    void shouldListCustomers() throws Exception {
        var response = new CustomerResponse(
                CustomerId.generate(), SANITIZED_DOCUMENT, DocumentType.CPF, "Maria Silva", "11999999999", EMAIL);
        var page = new PageResponse<>(List.of(response), false, 0, 20);

        when(this.listCustomersUseCase.execute(any())).thenReturn(page);

        this.mockMvc.perform(get(CustomerController.ENDPOINT).param("name", "Maria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value(EMAIL))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("[400] Não deve listar clientes com tamanho de página inválido")
    void shouldNotListCustomersWhenPageSizeExceedsLimit() throws Exception {
        this.mockMvc.perform(get(CustomerController.ENDPOINT).param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("size"))
                .andExpect(jsonPath("$.fields[0].code").value("Max"));
    }

    @Test
    @DisplayName("[204] Deve remover cliente com sucesso")
    void shouldDeleteCustomer() throws Exception {
        var id = CustomerId.generate();

        this.executeDelete(CustomerController.ENDPOINT + "/" + id)
                .andExpect(status().isNoContent());

        verify(this.deleteCustomerUseCase).execute(id);
    }

    @Test
    @DisplayName("[404] Não deve remover cliente inexistente")
    void shouldNotDeleteNonExistentCustomer() throws Exception {
        var id = CustomerId.generate();

        doThrow(new CustomerNotFoundException(id.toString())).when(this.deleteCustomerUseCase).execute(id);

        this.executeDelete(CustomerController.ENDPOINT + "/" + id)
                .andExpect(status().isNotFound());

        verify(this.deleteCustomerUseCase).execute(id);
    }
}
