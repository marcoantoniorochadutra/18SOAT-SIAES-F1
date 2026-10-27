package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.quotation.application.dto.QuotationResponse;
import com.fiap.siaes.quotation.application.usecase.GetQuotationByIdUseCase.GetQuotationByIdCommand;
import com.fiap.siaes.quotation.domain.exception.QuotationNotFoundException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static com.fiap.siaes.vehicle.VehicleTestFactory.PLATE;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Get Quotation By Id")
class GetQuotationByIdAppServiceTest {

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private GetQuotationByIdAppService getQuotationByIdAppService;

    @Test
    @DisplayName("Deve retornar o orçamento com cliente, veículo, itens e valor total")
    void shouldReturnQuotation() {
        Quotation quotation = createQuotation();
        Customer customer = createCustomer();
        Vehicle vehicle = createVehicle();

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);
        when(this.customerRepository.findByIdOrThrowNotFound(quotation.getCustomerId())).thenReturn(customer);
        when(this.vehicleRepository.findByIdOrThrowNotFound(quotation.getVehicleId())).thenReturn(vehicle);

        QuotationResponse response = this.getQuotationByIdAppService.execute(
                GetQuotationByIdCommand.of(quotation.getId(), UserTestFactory.authenticatedUserManager()));

        assertEquals(quotation.getId(), response.id());
        assertEquals(quotation.getWorkOrderId(), response.workOrderId());
        assertEquals(customer.getName(), response.customer().name());
        assertEquals(vehicle.getId(), response.vehicle().id());
        assertEquals(PLATE, response.vehicle().plate());
        assertEquals(QuotationStatus.AWAITING_APPROVAL, response.status());
        assertEquals(quotation.getTotalAmount(), response.totalAmount());
        assertEquals(1, response.supplies().size());
        assertEquals(1, response.maintenances().size());
    }

    @Test
    @DisplayName("Deve permitir que o cliente consulte o próprio orçamento")
    void shouldReturnQuotationToItsCustomer() {
        var customerId = CustomerId.generate();
        Quotation quotation = createQuotation(customerId, QuotationStatus.AWAITING_APPROVAL);

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);
        when(this.customerRepository.findByIdOrThrowNotFound(customerId)).thenReturn(createCustomer());
        when(this.vehicleRepository.findByIdOrThrowNotFound(quotation.getVehicleId())).thenReturn(createVehicle(customerId));

        QuotationResponse response = this.getQuotationByIdAppService.execute(
                GetQuotationByIdCommand.of(quotation.getId(), UserTestFactory.authenticatedUserCustomer(customerId)));

        assertEquals(quotation.getId(), response.id());
    }

    @Test
    @DisplayName("Não deve retornar orçamento de outro cliente")
    void shouldNotReturnQuotationOfAnotherCustomer() {
        Quotation quotation = createQuotation();
        var command = GetQuotationByIdCommand.of(quotation.getId(), UserTestFactory.authenticatedUserCustomer(CustomerId.generate()));

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);

        assertThrows(QuotationNotFoundException.class, () -> this.getQuotationByIdAppService.execute(command));
    }

    @Test
    @DisplayName("Não deve retornar ao cliente o próprio orçamento em rascunho")
    void shouldNotReturnDraftQuotationToItsCustomer() {
        var customerId = CustomerId.generate();
        Quotation quotation = createQuotation(customerId, QuotationStatus.DRAFT);
        var command = GetQuotationByIdCommand.of(quotation.getId(), UserTestFactory.authenticatedUserCustomer(customerId));

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);

        assertThrows(QuotationNotFoundException.class, () -> this.getQuotationByIdAppService.execute(command));

        verifyNoInteractions(this.customerRepository, this.vehicleRepository);
    }
}
