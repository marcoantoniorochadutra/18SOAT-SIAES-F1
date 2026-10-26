package com.fiap.siaes.quotation.infrastructure.subscriber;

import com.fiap.siaes.quotation.application.usecase.DiscardWorkOrderQuotationUseCase;
import com.fiap.siaes.quotation.application.usecase.DiscardWorkOrderQuotationUseCase.DiscardWorkOrderQuotationCommand;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.workorder.domain.event.WorkOrderCancelledEvent;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Subscriber - Work Order Cancelled")
class WorkOrderCancelledSubscriberTest {

    @Mock
    private DiscardWorkOrderQuotationUseCase discardWorkOrderQuotationUseCase;

    @InjectMocks
    private WorkOrderCancelledSubscriber workOrderCancelledSubscriber;

    @Test
    @DisplayName("Deve descartar o orçamento em rascunho da ordem de serviço cancelada")
    void shouldDiscardWorkOrderQuotation() {
        var event = WorkOrderCancelledEvent.of(WorkOrderId.generate(), UserId.generate(), Instant.now());

        this.workOrderCancelledSubscriber.on(event);

        verify(this.discardWorkOrderQuotationUseCase).execute(DiscardWorkOrderQuotationCommand.of(event.workOrderId(), event.cancelledBy()));
    }
}
