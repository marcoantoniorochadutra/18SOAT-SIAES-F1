package com.fiap.siaes.quotation.infrastructure.subscriber;

import com.fiap.siaes.quotation.application.usecase.DiscardWorkOrderQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.DiscardWorkOrderQuotationUseCase.DiscardWorkOrderQuotationCommand;
import com.fiap.siaes.workorder.domain.event.WorkOrderCancelledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkOrderCancelledSubscriber {

    private final DiscardWorkOrderQuotationUseCase discardWorkOrderQuotationUseCase;

    @EventListener
    public void on(WorkOrderCancelledEvent event) {
        this.discardWorkOrderQuotationUseCase.execute(DiscardWorkOrderQuotationCommand.of(event.workOrderId(), event.cancelledBy()));
    }
}
