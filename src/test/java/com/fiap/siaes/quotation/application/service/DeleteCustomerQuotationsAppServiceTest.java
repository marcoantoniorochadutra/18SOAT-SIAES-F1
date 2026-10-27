package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.application.service.helper.QuotationStockHelper;
import com.fiap.siaes.quotation.application.usecase.DeleteCustomerQuotationsUseCase.DeleteCustomerQuotationsCommand;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Delete Customer Quotations")
class DeleteCustomerQuotationsAppServiceTest {

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private QuotationStockHelper quotationStockHelper;

    @InjectMocks
    private DeleteCustomerQuotationsAppService deleteCustomerQuotationsAppService;

    @Test
    @DisplayName("Deve liberar a reserva dos orçamentos abertos e remover todos os orçamentos do cliente")
    void shouldReleaseOpenReservationsAndDeleteQuotations() {
        var customerId = CustomerId.generate();
        var deletedBy = UserId.generate();
        Quotation draft = createQuotation(customerId, QuotationStatus.DRAFT);
        Quotation awaitingApproval = createQuotation(customerId, QuotationStatus.AWAITING_APPROVAL);
        Quotation approved = createQuotation(customerId, QuotationStatus.APPROVED);

        when(this.quotationRepository.findAllByCustomerId(customerId)).thenReturn(List.of(draft, awaitingApproval, approved));

        this.deleteCustomerQuotationsAppService.execute(DeleteCustomerQuotationsCommand.of(customerId, deletedBy));

        InOrder inOrder = inOrder(this.quotationStockHelper, this.quotationRepository);
        inOrder.verify(this.quotationStockHelper).releaseAll(draft, deletedBy, null);
        inOrder.verify(this.quotationStockHelper).releaseAll(awaitingApproval, deletedBy, null);
        inOrder.verify(this.quotationRepository).deleteAllByCustomerId(customerId);
        verify(this.quotationStockHelper, never()).releaseAll(eq(approved), any(), any());
    }
}
