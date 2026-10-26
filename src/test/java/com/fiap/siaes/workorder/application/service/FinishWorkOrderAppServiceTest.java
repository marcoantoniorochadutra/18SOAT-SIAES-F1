package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.workorder.application.usecase.FinishWorkOrderUseCase.FinishWorkOrderCommand;
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
@DisplayName("UnitTest - App Service - Finish Work Order")
class FinishWorkOrderAppServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private FinishWorkOrderAppService finishWorkOrderAppService;

    @Test
    @DisplayName("Deve finalizar a ordem de serviço em execução")
    void shouldFinishWorkOrder() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_EXECUTION);

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        this.finishWorkOrderAppService.execute(FinishWorkOrderCommand.of(workOrder.getId()));

        assertEquals(WorkOrderStatus.FINISHED, workOrder.getLastStatus());
        verify(this.workOrderRepository).save(workOrder);
    }

    @Test
    @DisplayName("Não deve finalizar ordem de serviço fora da execução")
    void shouldNotFinishWhenNotInExecution() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_DIAGNOSIS);
        var command = FinishWorkOrderCommand.of(workOrder.getId());

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        assertThrows(WorkOrderStatusTransitionInvalidException.class, () -> this.finishWorkOrderAppService.execute(command));

        verify(this.workOrderRepository, never()).save(any());
    }
}
