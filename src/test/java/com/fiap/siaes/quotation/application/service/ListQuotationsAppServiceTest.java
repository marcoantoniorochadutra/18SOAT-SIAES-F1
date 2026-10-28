package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.application.dto.ListQuotationResponse;
import com.fiap.siaes.quotation.application.dto.QuotationFilter;
import com.fiap.siaes.quotation.application.port.QuotationQueryPort;
import com.fiap.siaes.quotation.application.usecase.ListQuotationsUseCase.ListQuotationsCommand;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.sk.infrastructure.persistence.PageResponses;
import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.SliceImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.fiap.siaes.quotation.QuotationTestFactory.DESCRIPTION;
import static com.fiap.siaes.user.UserTestFactory.authenticatedUserCustomer;
import static com.fiap.siaes.user.UserTestFactory.authenticatedUserManager;
import static com.fiap.siaes.vehicle.VehicleTestFactory.PLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - List Quotations")
class ListQuotationsAppServiceTest {

    @Mock
    private QuotationQueryPort quotationQueryPort;

    @InjectMocks
    private ListQuotationsAppService listQuotationsAppService;

    @Test
    @DisplayName("Deve listar os orçamentos da página com os filtros informados")
    void shouldListQuotationsPageWithFilter() {
        var filter = QuotationFilter.of(WorkOrderId.generate(), CustomerId.generate(), VehicleId.generate(),
                                        QuotationStatus.AWAITING_APPROVAL);
        var pageQuery = PageQuery.of(0, 10);
        var quotations = List.of(ListQuotationResponse.builder()
                                         .id(QuotationId.generate())
                                         .workOrderId(filter.workOrderId())
                                         .customer(CustomerReferenceResponse.of(filter.customerId(), "Maria Silva"))
                                         .vehicle(VehicleReferenceResponse.of(filter.vehicleId(), PLATE, "Fiat", "Uno"))
                                         .description(DESCRIPTION)
                                         .status(QuotationStatus.AWAITING_APPROVAL)
                                         .totalAmount(new BigDecimal("125.90"))
                                         .createdAt(Instant.now())
                                         .build());

        when(this.quotationQueryPort.findAllByFilter(filter, true, pageQuery))
                .thenReturn(PageResponses.from(new SliceImpl<>(quotations)));

        PageResponse<ListQuotationResponse> response = this.listQuotationsAppService.execute(
                ListQuotationsCommand.of(filter, pageQuery, authenticatedUserManager()));

        assertEquals(quotations, response.content());
        assertFalse(response.hasNext());
    }

    @Test
    @DisplayName("Deve listar para o cliente apenas os próprios orçamentos, sem rascunhos")
    void shouldListOnlyOwnNonDraftQuotationsForCustomer() {
        var customerId = CustomerId.generate();
        var filter = QuotationFilter.of(null, CustomerId.generate(), null, null);
        var pageQuery = PageQuery.of(0, 10);
        PageResponse<ListQuotationResponse> page = PageResponses.from(new SliceImpl<>(List.of()));

        when(this.quotationQueryPort.findAllByFilter(filter.withCustomerId(customerId), false, pageQuery)).thenReturn(page);

        PageResponse<ListQuotationResponse> response = this.listQuotationsAppService.execute(
                ListQuotationsCommand.of(filter, pageQuery, authenticatedUserCustomer(customerId)));

        assertEquals(page, response);
    }
}
