package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.workorder.application.usecase.ApproveWorkOrderQuotationUseCase.ApproveWorkOrderQuotationCommand;
import com.fiap.siaes.workorder.domain.exception.WorkOrderStatusTransitionInvalidException;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.workorder.WorkOrderTestFactory.createWorkOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Approve Work Order Quotation")
class ApproveWorkOrderQuotationAppServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private ApproveWorkOrderQuotationAppService approveWorkOrderQuotationAppService;

    @Test
    @DisplayName("Deve iniciar a execução da ordem de serviço quando o orçamento for aprovado")
    void shouldStartExecution() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.AWAITING_APPROVAL);

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        this.approveWorkOrderQuotationAppService.execute(ApproveWorkOrderQuotationCommand.of(workOrder.getId()));

        assertEquals(WorkOrderStatus.IN_EXECUTION, workOrder.getLastStatus());
        verify(this.workOrderRepository).save(workOrder);
    }

    @Test
    @DisplayName("Não deve iniciar a execução de ordem de serviço que não aguarda aprovação")
    void shouldNotStartExecutionWhenNotAwaitingApproval() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.RECEIVED);

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        assertThrows(WorkOrderStatusTransitionInvalidException.class,
                     () -> this.approveWorkOrderQuotationAppService.execute(ApproveWorkOrderQuotationCommand.of(workOrder.getId())));

        verify(this.workOrderRepository, never()).save(any());
    }
}
