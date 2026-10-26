package com.fiap.siaes.workorder.infrastructure.subscriber;

import com.fiap.siaes.quotation.domain.event.QuotationApprovedEvent;
import com.fiap.siaes.workorder.application.usecase.ApproveWorkOrderQuotationUseCase;
import com.fiap.siaes.workorder.application.usecase.ApproveWorkOrderQuotationUseCase.ApproveWorkOrderQuotationCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuotationApprovedSubscriber {

    private final ApproveWorkOrderQuotationUseCase approveWorkOrderQuotationUseCase;

    @EventListener
    public void on(QuotationApprovedEvent event) {
        this.approveWorkOrderQuotationUseCase.execute(ApproveWorkOrderQuotationCommand.of(event.workOrderId()));
    }
}
