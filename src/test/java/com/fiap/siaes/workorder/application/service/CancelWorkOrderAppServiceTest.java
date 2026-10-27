package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import com.fiap.siaes.workorder.application.port.WorkOrderQuotationPort;
import com.fiap.siaes.workorder.application.usecase.CancelWorkOrderUseCase.CancelWorkOrderCommand;
import com.fiap.siaes.workorder.domain.event.WorkOrderCancelledEvent;
import com.fiap.siaes.workorder.domain.exception.WorkOrderNotFoundException;
import com.fiap.siaes.workorder.domain.exception.WorkOrderOpenQuotationException;
import com.fiap.siaes.workorder.domain.exception.WorkOrderStatusTransitionInvalidException;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.user.UserTestFactory.authenticatedUserAdmin;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.workorder.WorkOrderTestFactory.createWorkOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Cancel Work Order")
class CancelWorkOrderAppServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private WorkOrderQuotationPort workOrderQuotationPort;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private CancelWorkOrderAppService cancelWorkOrderAppService;

    @ParameterizedTest
    @EnumSource(value = WorkOrderStatus.class, names = {"RECEIVED", "IN_DIAGNOSIS", "AWAITING_APPROVAL"})
    @DisplayName("Deve cancelar a ordem de serviço sem orçamento aguardando aprovação e publicar o evento")
    void shouldCancelWorkOrder(WorkOrderStatus status) {
        WorkOrder workOrder = createWorkOrder(status);
        var authenticatedUser = authenticatedUserAdmin();

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);
        when(this.workOrderQuotationPort.hasQuotationAwaitingApproval(workOrder.getId())).thenReturn(false);

        this.cancelWorkOrderAppService.execute(CancelWorkOrderCommand.of(workOrder.getId(), authenticatedUser));

        assertEquals(WorkOrderStatus.CANCELLED, workOrder.getLastStatus());
        verify(this.workOrderRepository).save(workOrder);
        verify(this.domainEventPublisher).publish(argThat(events -> events.size() == 1
                && events.getFirst() instanceof WorkOrderCancelledEvent event
                && workOrder.getId().equals(event.workOrderId())
                && authenticatedUser.id().equals(event.cancelledBy())));
    }

    @Test
    @DisplayName("Não deve cancelar a ordem de serviço com orçamento aguardando aprovação")
    void shouldNotCancelWorkOrderWithQuotationAwaitingApproval() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.AWAITING_APPROVAL);
        var command = CancelWorkOrderCommand.of(workOrder.getId(), authenticatedUserAdmin());

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);
        when(this.workOrderQuotationPort.hasQuotationAwaitingApproval(workOrder.getId())).thenReturn(true);

        var exception = assertThrows(WorkOrderOpenQuotationException.class,
                () -> this.cancelWorkOrderAppService.execute(command));

        assertExceptionParameters(exception, workOrder.getId().toString());
        assertEquals(WorkOrderStatus.AWAITING_APPROVAL, workOrder.getLastStatus());
        verify(this.workOrderRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve cancelar a ordem de serviço em execução")
    void shouldNotCancelWorkOrderInExecution() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_EXECUTION);
        var command = CancelWorkOrderCommand.of(workOrder.getId(), authenticatedUserAdmin());

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);
        when(this.workOrderQuotationPort.hasQuotationAwaitingApproval(workOrder.getId())).thenReturn(false);

        assertThrows(WorkOrderStatusTransitionInvalidException.class,
                () -> this.cancelWorkOrderAppService.execute(command));

        verify(this.workOrderRepository, never()).save(any());
        verifyNoInteractions(this.domainEventPublisher);
    }

    @Test
    @DisplayName("Não deve cancelar a ordem de serviço inexistente")
    void shouldNotCancelNonExistentWorkOrder() {
        var id = WorkOrderId.generate();
        var command = CancelWorkOrderCommand.of(id, authenticatedUserAdmin());

        when(this.workOrderRepository.findByIdOrThrowNotFound(id)).thenThrow(new WorkOrderNotFoundException(id.toString()));

        assertThrows(WorkOrderNotFoundException.class, () -> this.cancelWorkOrderAppService.execute(command));

        verifyNoInteractions(this.workOrderQuotationPort, this.domainEventPublisher);
        verify(this.workOrderRepository, never()).save(any());
    }
}
