package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.application.usecase.CreateCustomerUseCase.CreateCustomerCommand;
import com.fiap.siaes.customer.domain.exception.CustomerDocumentAlreadyExistsException;
import com.fiap.siaes.customer.domain.exception.CustomerEmailAlreadyExistsException;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.sk.document.domain.Document;
import com.fiap.siaes.sk.document.domain.enums.DocumentType;
import com.fiap.siaes.sk.document.domain.exception.InvalidDocumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Create Customer")
class CreateCustomerAppServiceTest {

    private static final String VALID_DOCUMENT = "529.982.247-25";
    private static final String SANITIZED_DOCUMENT = "52998224725";
    private static final String EMAIL = "maria@email.com";

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CreateCustomerAppService createCustomerAppService;

    @Test
    @DisplayName("Deve criar um cliente com sucesso")
    void shouldCreateCustomerSuccessfully() {

        var command = CreateCustomerCommand.builder()
                .document(VALID_DOCUMENT)
                .name("Maria Silva")
                .phone("11999999999")
                .email(EMAIL)
                .build();

        CustomerId customerId = this.createCustomerAppService.execute(command);

        Customer savedCustomer = captureSave(this.customerRepository, Customer.class);

        assertEquals(savedCustomer.getId(), customerId);
        assertEquals(SANITIZED_DOCUMENT, savedCustomer.getDocument().getValue());
        assertEquals(DocumentType.CPF, savedCustomer.getDocument().getDocumentType());
        assertEquals(command.name(), savedCustomer.getName());
        assertEquals(command.phone(), savedCustomer.getPhone());
        assertEquals(command.email(), savedCustomer.getEmail());
        assertNotNull(savedCustomer.getCreatedAt());
        assertEquals(savedCustomer.getCreatedAt(), savedCustomer.getUpdatedAt());
    }

    @Test
    @DisplayName("Não deve criar cliente quando o e-mail já estiver cadastrado")
    void shouldNotCreateCustomerWhenEmailAlreadyExists() {
        var command = CreateCustomerCommand.builder()
                .document(VALID_DOCUMENT)
                .name("Maria Silva")
                .phone("11999999999")
                .email(EMAIL)
                .build();

        when(this.customerRepository.existsByEmail(EMAIL)).thenReturn(true);

        var exception = assertThrows(CustomerEmailAlreadyExistsException.class,
                () -> this.createCustomerAppService.execute(command));

        assertExceptionParameters(exception, EMAIL);
        verify(this.customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar cliente quando o documento já estiver cadastrado")
    void shouldNotCreateCustomerWhenDocumentAlreadyExists() {
        var command = CreateCustomerCommand.builder()
                .document(VALID_DOCUMENT)
                .name("Maria Silva")
                .phone("11999999999")
                .email(EMAIL)
                .build();

        when(this.customerRepository.existsByDocument(Document.recreate(SANITIZED_DOCUMENT))).thenReturn(true);

        var exception = assertThrows(CustomerDocumentAlreadyExistsException.class,
                () -> this.createCustomerAppService.execute(command));

        assertExceptionParameters(exception, SANITIZED_DOCUMENT);
        verify(this.customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar cliente com documento inválido")
    void shouldNotCreateCustomerWithInvalidDocument() {
        var command = CreateCustomerCommand.builder()
                .document("111.111.111-12")
                .name("Maria Silva")
                .email(EMAIL)
                .build();

        assertThrows(InvalidDocumentException.class,
                () -> this.createCustomerAppService.execute(command));

        verifyNoInteractions(this.customerRepository);
    }
}
