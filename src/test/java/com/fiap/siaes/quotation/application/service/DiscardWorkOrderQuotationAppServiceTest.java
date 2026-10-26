package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.application.service.helper.QuotationStockHelper;
import com.fiap.siaes.quotation.application.usecase.DiscardWorkOrderQuotationUseCase.DiscardWorkOrderQuotationCommand;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Discard Work Order Quotation")
class DiscardWorkOrderQuotationAppServiceTest {

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private QuotationStockHelper quotationStockHelper;

    @InjectMocks
    private DiscardWorkOrderQuotationAppService discardWorkOrderQuotationAppService;

    @Test
    @DisplayName("Deve descartar o orçamento em rascunho da ordem de serviço e liberar a reserva de estoque")
    void shouldDiscardDraftQuotation() {
        Quotation quotation = createQuotation(CustomerId.generate(), QuotationStatus.DRAFT);
        var discardedBy = UserId.generate();

        when(this.quotationRepository.findDraftByWorkOrderId(quotation.getWorkOrderId())).thenReturn(Optional.of(quotation));

        this.discardWorkOrderQuotationAppService.execute(DiscardWorkOrderQuotationCommand.of(quotation.getWorkOrderId(), discardedBy));

        assertEquals(QuotationStatus.DISCARDED, quotation.getLastStatus());
        verify(this.quotationRepository).save(quotation);
        verify(this.quotationStockHelper).releaseAll(quotation, discardedBy, null);
    }

    @Test
    @DisplayName("Não deve fazer nada quando a ordem de serviço não possuir orçamento em rascunho")
    void shouldDoNothingWithoutDraftQuotation() {
        var workOrderId = WorkOrderId.generate();

        when(this.quotationRepository.findDraftByWorkOrderId(workOrderId)).thenReturn(Optional.empty());

        this.discardWorkOrderQuotationAppService.execute(DiscardWorkOrderQuotationCommand.of(workOrderId, UserId.generate()));

        verify(this.quotationRepository, never()).save(any());
        verifyNoInteractions(this.quotationStockHelper);
    }
}
