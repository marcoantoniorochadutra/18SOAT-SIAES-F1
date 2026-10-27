package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.workorder.application.usecase.RegisterWorkOrderQuotationUseCase.RegisterWorkOrderQuotationCommand;
import com.fiap.siaes.workorder.domain.exception.WorkOrderNotFoundException;
import com.fiap.siaes.workorder.domain.exception.WorkOrderStatusTransitionInvalidException;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.workorder.WorkOrderTestFactory.createDiagnosedWorkOrder;
import static com.fiap.siaes.workorder.WorkOrderTestFactory.createWorkOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Register Work Order Quotation")
class RegisterWorkOrderQuotationAppServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private RegisterWorkOrderQuotationAppService registerWorkOrderQuotationAppService;

    @Test
    @DisplayName("Deve registrar o orçamento e deixar a ordem de serviço aguardando aprovação")
    void shouldRegisterWorkOrderQuotation() {
        WorkOrder workOrder = createDiagnosedWorkOrder();

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        this.registerWorkOrderQuotationAppService.execute(RegisterWorkOrderQuotationCommand.of(workOrder.getId()));

        assertEquals(WorkOrderStatus.AWAITING_APPROVAL, workOrder.getLastStatus());
        verify(this.workOrderRepository).save(workOrder);
    }

    @Test
    @DisplayName("Deve aceitar um novo orçamento quando a ordem de serviço já aguarda aprovação")
    void shouldRegisterAnotherQuotationWhileAwaitingApproval() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.AWAITING_APPROVAL);

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        this.registerWorkOrderQuotationAppService.execute(RegisterWorkOrderQuotationCommand.of(workOrder.getId()));

        assertEquals(WorkOrderStatus.AWAITING_APPROVAL, workOrder.getLastStatus());
        assertEquals(1, workOrder.getStatusHistory().size());
    }

    @Test
    @DisplayName("Não deve salvar quando a ordem de serviço já estiver em execução")
    void shouldNotSaveWhenWorkOrderIsNotCreated() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_EXECUTION);

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        assertThrows(WorkOrderStatusTransitionInvalidException.class,
                () -> this.registerWorkOrderQuotationAppService.execute(RegisterWorkOrderQuotationCommand.of(workOrder.getId())));

        verify(this.workOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve alterar quando a ordem de serviço não existir")
    void shouldNotChangeWhenWorkOrderDoesNotExist() {
        var id = WorkOrderId.generate();

        when(this.workOrderRepository.findByIdOrThrowNotFound(id)).thenThrow(new WorkOrderNotFoundException(id.toString()));

        assertThrows(WorkOrderNotFoundException.class,
                () -> this.registerWorkOrderQuotationAppService.execute(RegisterWorkOrderQuotationCommand.of(id)));

        verify(this.workOrderRepository, never()).save(any());
    }
}
