package com.fiap.siaes.workorder.infrastructure.subscriber;

import com.fiap.siaes.quotation.domain.event.QuotationApprovedEvent;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.workorder.application.usecase.ApproveWorkOrderQuotationUseCase;
import com.fiap.siaes.workorder.application.usecase.ApproveWorkOrderQuotationUseCase.ApproveWorkOrderQuotationCommand;
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
@DisplayName("UnitTest - Subscriber - Quotation Approved")
class QuotationApprovedSubscriberTest {

    @Mock
    private ApproveWorkOrderQuotationUseCase approveWorkOrderQuotationUseCase;

    @InjectMocks
    private QuotationApprovedSubscriber quotationApprovedSubscriber;

    @Test
    @DisplayName("Deve iniciar a execução da ordem de serviço do orçamento aprovado")
    void shouldApproveWorkOrderQuotation() {
        var event = QuotationApprovedEvent.of(QuotationId.generate(), WorkOrderId.generate(), Instant.now());

        this.quotationApprovedSubscriber.on(event);

        verify(this.approveWorkOrderQuotationUseCase).execute(ApproveWorkOrderQuotationCommand.of(event.workOrderId()));
    }
}
