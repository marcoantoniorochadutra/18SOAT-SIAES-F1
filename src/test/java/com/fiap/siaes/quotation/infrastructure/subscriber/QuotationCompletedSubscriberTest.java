package com.fiap.siaes.quotation.infrastructure.subscriber;

import com.fiap.siaes.quotation.application.usecase.SendQuotationForApprovalUseCase;
import com.fiap.siaes.quotation.application.usecase.SendQuotationForApprovalUseCase.SendQuotationForApprovalCommand;
import com.fiap.siaes.quotation.domain.event.QuotationCompletedEvent;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Subscriber - Quotation Completed (approval)")
class QuotationCompletedSubscriberTest {

    @Mock
    private SendQuotationForApprovalUseCase sendQuotationForApprovalUseCase;

    @InjectMocks
    private QuotationCompletedSubscriber quotationCompletedSubscriber;

    @Test
    @DisplayName("Deve enviar o orçamento concluído para aprovação do cliente")
    void shouldSendQuotationForApproval() {
        var event = QuotationCompletedEvent.of(QuotationId.generate(), WorkOrderId.generate(), Instant.now());

        this.quotationCompletedSubscriber.on(event);

        verify(this.sendQuotationForApprovalUseCase).execute(SendQuotationForApprovalCommand.of(event.quotationId()));
    }

    @Test
    @DisplayName("Não deve propagar falha no envio, pois o orçamento já foi gravado")
    void shouldNotPropagateFailure() {
        var event = QuotationCompletedEvent.of(QuotationId.generate(), WorkOrderId.generate(), Instant.now());

        doThrow(new IllegalStateException("smtp down"))
                .when(this.sendQuotationForApprovalUseCase).execute(SendQuotationForApprovalCommand.of(event.quotationId()));

        assertDoesNotThrow(() -> this.quotationCompletedSubscriber.on(event));
    }
}
