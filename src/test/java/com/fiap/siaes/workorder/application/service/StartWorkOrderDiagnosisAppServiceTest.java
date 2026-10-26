package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.workorder.application.usecase.StartWorkOrderDiagnosisUseCase.StartWorkOrderDiagnosisCommand;
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
@DisplayName("UnitTest - App Service - Start Work Order Diagnosis")
class StartWorkOrderDiagnosisAppServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private StartWorkOrderDiagnosisAppService startWorkOrderDiagnosisAppService;

    @Test
    @DisplayName("Deve iniciar o diagnóstico da ordem de serviço recebida")
    void shouldStartDiagnosis() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.RECEIVED);

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        this.startWorkOrderDiagnosisAppService.execute(StartWorkOrderDiagnosisCommand.of(workOrder.getId()));

        assertEquals(WorkOrderStatus.IN_DIAGNOSIS, workOrder.getLastStatus());
        verify(this.workOrderRepository).save(workOrder);
    }

    @Test
    @DisplayName("Não deve iniciar o diagnóstico de ordem de serviço que já saiu de recebida")
    void shouldNotStartDiagnosisWhenNotReceived() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.AWAITING_APPROVAL);
        var command = StartWorkOrderDiagnosisCommand.of(workOrder.getId());

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        assertThrows(WorkOrderStatusTransitionInvalidException.class, () -> this.startWorkOrderDiagnosisAppService.execute(command));

        verify(this.workOrderRepository, never()).save(any());
    }
}
