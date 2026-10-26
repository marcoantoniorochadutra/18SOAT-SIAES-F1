package com.fiap.siaes.quotation.infrastructure.subscriber;

import com.fiap.siaes.quotation.application.usecase.SendQuotationForApprovalUseCase;
import com.fiap.siaes.quotation.application.usecase.SendQuotationForApprovalUseCase.SendQuotationForApprovalCommand;
import com.fiap.siaes.quotation.domain.event.QuotationCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.fiap.siaes.sk.infraestructure.config.AsyncConfig.DOMAIN_EVENT_EXECUTOR;

@Slf4j
@Component("quotationApprovalQuotationCompletedSubscriber")
@RequiredArgsConstructor
public class QuotationCompletedSubscriber {

    private final SendQuotationForApprovalUseCase sendQuotationForApprovalUseCase;

    @Async(DOMAIN_EVENT_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(QuotationCompletedEvent event) {
        try {
            this.sendQuotationForApprovalUseCase.execute(SendQuotationForApprovalCommand.of(event.quotationId()));
        } catch (RuntimeException exception) {
            log.error("Failed to send quotation {} for approval", event.quotationId(), exception);
        }
    }
}
