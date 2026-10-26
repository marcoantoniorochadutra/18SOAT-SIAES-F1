package com.fiap.siaes.workorder.infrastructure.subscriber;

import com.fiap.siaes.quotation.domain.event.QuotationCompletedEvent;
import com.fiap.siaes.workorder.application.usecase.RegisterWorkOrderQuotationUseCase;
import com.fiap.siaes.workorder.application.usecase.RegisterWorkOrderQuotationUseCase.RegisterWorkOrderQuotationCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuotationCompletedSubscriber {

    private final RegisterWorkOrderQuotationUseCase registerWorkOrderQuotationUseCase;

    @EventListener
    public void on(QuotationCompletedEvent event) {
        this.registerWorkOrderQuotationUseCase.execute(RegisterWorkOrderQuotationCommand.of(event.workOrderId()));
    }
}
