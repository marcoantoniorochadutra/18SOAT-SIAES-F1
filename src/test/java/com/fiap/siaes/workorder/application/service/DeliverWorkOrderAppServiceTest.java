package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.workorder.application.usecase.DeliverWorkOrderUseCase.DeliverWorkOrderCommand;
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
@DisplayName("UnitTest - App Service - Deliver Work Order")
class DeliverWorkOrderAppServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private DeliverWorkOrderAppService deliverWorkOrderAppService;

    @Test
    @DisplayName("Deve registrar a entrega da ordem de serviço finalizada")
    void shouldDeliverWorkOrder() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.FINISHED);

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        this.deliverWorkOrderAppService.execute(DeliverWorkOrderCommand.of(workOrder.getId()));

        assertEquals(WorkOrderStatus.DELIVERED, workOrder.getLastStatus());
        verify(this.workOrderRepository).save(workOrder);
    }

    @Test
    @DisplayName("Não deve entregar ordem de serviço que não foi finalizada")
    void shouldNotDeliverWhenNotFinished() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_EXECUTION);
        var command = DeliverWorkOrderCommand.of(workOrder.getId());

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        assertThrows(WorkOrderStatusTransitionInvalidException.class, () -> this.deliverWorkOrderAppService.execute(command));

        verify(this.workOrderRepository, never()).save(any());
    }
}
