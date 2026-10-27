package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalResponse;
import com.fiap.siaes.quotation.application.service.helper.QuotationApprovalTokenHelper;
import com.fiap.siaes.quotation.application.usecase.GetQuotationByApprovalTokenUseCase.GetQuotationByApprovalTokenCommand;
import com.fiap.siaes.quotation.domain.exception.QuotationApprovalTokenInvalidException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.quotation.QuotationTestFactory.createApprovalToken;
import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Get Quotation By Approval Token")
class GetQuotationByApprovalTokenAppServiceTest {

    private static final String RAW_TOKEN = "raw-token";

    @Mock
    private QuotationApprovalTokenHelper quotationApprovalTokenHelper;

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private GetQuotationByApprovalTokenAppService getQuotationByApprovalTokenAppService;

    @Test
    @DisplayName("Deve retornar o orçamento com o nome do cliente e o veículo")
    void shouldReturnQuotation() {
        Customer customer = createCustomer();
        Quotation quotation = createQuotation(customer.getId(), QuotationStatus.AWAITING_APPROVAL);
        Vehicle vehicle = createVehicle(customer.getId());
        QuotationApprovalToken token = createApprovalToken(quotation.getId());

        when(this.quotationApprovalTokenHelper.findActive(RAW_TOKEN)).thenReturn(token);
        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);
        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);
        when(this.vehicleRepository.findByIdOrThrowNotFound(quotation.getVehicleId())).thenReturn(vehicle);

        QuotationApprovalResponse response = this.getQuotationByApprovalTokenAppService.execute(
                GetQuotationByApprovalTokenCommand.of(RAW_TOKEN));

        assertEquals(quotation.getId(), response.quotation().id());
        assertEquals(customer.getName(), response.quotation().customer().name());
        assertEquals(vehicle.getPlate().getValue(), response.quotation().vehicle().plate());
        assertEquals(vehicle.getBrand(), response.quotation().vehicle().brand());
        assertEquals(vehicle.getModel(), response.quotation().vehicle().model());
        assertEquals(token.getExpiresAt(), response.expiresAt());
    }

    @Test
    @DisplayName("Não deve exibir orçamento que já foi decidido")
    void shouldNotReturnDecidedQuotation() {
        Quotation quotation = createQuotation(createCustomer().getId(), QuotationStatus.APPROVED);
        QuotationApprovalToken token = createApprovalToken(quotation.getId());

        when(this.quotationApprovalTokenHelper.findActive(RAW_TOKEN)).thenReturn(token);
        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);

        assertThrows(QuotationApprovalTokenInvalidException.class,
                     () -> this.getQuotationByApprovalTokenAppService.execute(GetQuotationByApprovalTokenCommand.of(RAW_TOKEN)));

        verifyNoInteractions(this.customerRepository, this.vehicleRepository);
    }
}
