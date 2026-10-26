package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.application.usecase.CompleteQuotationUseCase.CompleteQuotationCommand;
import com.fiap.siaes.quotation.domain.event.QuotationCompletedEvent;
import com.fiap.siaes.quotation.domain.exception.QuotationNotEditableException;
import com.fiap.siaes.quotation.domain.exception.QuotationNotFoundException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Complete Quotation")
class CompleteQuotationAppServiceTest {

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private CompleteQuotationAppService completeQuotationAppService;

    @Test
    @DisplayName("Deve concluir o orçamento em rascunho e publicar o evento de orçamento concluído")
    void shouldCompleteQuotation() {
        Quotation quotation = createQuotation(CustomerId.generate(), QuotationStatus.DRAFT);

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);

        this.completeQuotationAppService.execute(CompleteQuotationCommand.of(quotation.getId()));

        assertEquals(QuotationStatus.AWAITING_APPROVAL, quotation.getLastStatus());
        verify(this.quotationRepository).save(quotation);
        verify(this.domainEventPublisher).publish(argThat(events -> events.size() == 1
                && events.getFirst() instanceof QuotationCompletedEvent event
                && quotation.getWorkOrderId().equals(event.workOrderId())));
    }

    @Test
    @DisplayName("Não deve concluir orçamento que não está em rascunho")
    void shouldNotCompleteQuotationNotDraft() {
        Quotation quotation = createQuotation(CustomerId.generate(), QuotationStatus.AWAITING_APPROVAL);
        var command = CompleteQuotationCommand.of(quotation.getId());

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);

        assertThrows(QuotationNotEditableException.class, () -> this.completeQuotationAppService.execute(command));

        verify(this.quotationRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve concluir orçamento inexistente")
    void shouldNotCompleteNonExistentQuotation() {
        var id = QuotationId.generate();
        var command = CompleteQuotationCommand.of(id);

        when(this.quotationRepository.findByIdOrThrowNotFound(id)).thenThrow(new QuotationNotFoundException(id.toString()));

        assertThrows(QuotationNotFoundException.class, () -> this.completeQuotationAppService.execute(command));

        verifyNoInteractions(this.domainEventPublisher);
    }
}
