package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.quotation.application.service.helper.QuotationApprovalTokenHelper;
import com.fiap.siaes.quotation.application.service.helper.QuotationStockHelper;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase.DecideQuotationCommand;
import com.fiap.siaes.quotation.domain.event.QuotationApprovedEvent;
import com.fiap.siaes.quotation.domain.event.QuotationRejectedEvent;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecision;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecisionChannel;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import com.fiap.siaes.user.domain.model.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Decide Quotation")
class DecideQuotationAppServiceTest {

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private QuotationApprovalTokenHelper quotationApprovalTokenHelper;

    @Mock
    private QuotationStockHelper quotationStockHelper;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private DecideQuotationAppService decideQuotationAppService;

    @Test
    @DisplayName("Deve aprovar o orçamento, dar baixa na reserva de estoque, revogar o link e publicar o evento de aprovação")
    void shouldApproveQuotation() {
        Quotation quotation = createQuotation();
        var decidedBy = UserId.generate();

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);

        this.decideQuotationAppService.execute(this.command(quotation, QuotationDecision.APPROVE, decidedBy));

        assertEquals(QuotationStatus.APPROVED, quotation.getLastStatus());
        verify(this.quotationRepository).save(quotation);
        verify(this.quotationApprovalTokenHelper).revokeCurrent(quotation.getId());
        verify(this.quotationStockHelper).consumeAll(quotation, decidedBy, null);
        verify(this.quotationStockHelper, never()).releaseAll(any(), any(), any());
        verify(this.domainEventPublisher).publish(argThat(events -> events.size() == 1
                && events.getFirst() instanceof QuotationApprovedEvent));
    }

    @Test
    @DisplayName("Deve rejeitar o orçamento e liberar a reserva de estoque")
    void shouldRejectQuotation() {
        Quotation quotation = createQuotation();
        var decidedBy = UserId.generate();

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);

        this.decideQuotationAppService.execute(this.command(quotation, QuotationDecision.REJECT, decidedBy));

        assertEquals(QuotationStatus.REJECTED, quotation.getLastStatus());
        assertEquals("Muito caro", quotation.getRejectionReason());
        verify(this.quotationStockHelper).releaseAll(quotation, decidedBy, null);
        verify(this.quotationStockHelper, never()).consumeAll(any(), any(), any());
        verify(this.quotationRepository).save(quotation);
        verify(this.quotationApprovalTokenHelper).revokeCurrent(quotation.getId());
        verify(this.domainEventPublisher).publish(argThat(events -> events.size() == 1
                && events.getFirst() instanceof QuotationRejectedEvent));
    }

    private DecideQuotationCommand command(Quotation quotation, QuotationDecision decision, UserId decidedBy) {
        return DecideQuotationCommand.builder()
                .quotationId(quotation.getId())
                .decision(decision)
                .reason("Muito caro")
                .channel(QuotationDecisionChannel.CUSTOMER_ACCOUNT)
                .decidedBy(decidedBy)
                .build();
    }
}
