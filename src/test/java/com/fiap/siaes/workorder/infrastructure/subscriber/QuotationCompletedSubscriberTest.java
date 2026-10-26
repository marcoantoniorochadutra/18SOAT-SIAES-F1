package com.fiap.siaes.workorder.infrastructure.subscriber;

import com.fiap.siaes.quotation.domain.event.QuotationCompletedEvent;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.workorder.application.usecase.RegisterWorkOrderQuotationUseCase;
import com.fiap.siaes.workorder.application.usecase.RegisterWorkOrderQuotationUseCase.RegisterWorkOrderQuotationCommand;
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
@DisplayName("UnitTest - Subscriber - Quotation Completed")
class QuotationCompletedSubscriberTest {

    @Mock
    private RegisterWorkOrderQuotationUseCase registerWorkOrderQuotationUseCase;

    @InjectMocks
    private QuotationCompletedSubscriber quotationCompletedSubscriber;

    @Test
    @DisplayName("Deve registrar o orçamento concluído na ordem de serviço")
    void shouldRegisterWorkOrderQuotation() {
        var event = QuotationCompletedEvent.of(QuotationId.generate(), WorkOrderId.generate(), Instant.now());

        this.quotationCompletedSubscriber.on(event);

        verify(this.registerWorkOrderQuotationUseCase).execute(RegisterWorkOrderQuotationCommand.of(event.workOrderId()));
    }
}
