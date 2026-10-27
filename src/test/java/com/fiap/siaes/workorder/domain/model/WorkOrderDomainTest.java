package com.fiap.siaes.workorder.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.event.WorkOrderCancelledEvent;
import com.fiap.siaes.workorder.domain.exception.WorkOrderStatusTransitionInvalidException;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.workorder.WorkOrderTestFactory.createWorkOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - Work Order")
class WorkOrderDomainTest {

    @Test
    @DisplayName("Deve criar uma ordem de serviço recebida com histórico de status")
    void shouldCreateReceivedWorkOrder() {
        var customerId = CustomerId.generate();
        var vehicleId = VehicleId.generate();

        WorkOrder workOrder = WorkOrder.builder()
                .customerId(customerId)
                .vehicleId(vehicleId)
                .build();

        assertNotNull(workOrder.getId());
        assertNotNull(workOrder.getCreatedAt());
        assertNull(workOrder.getFinishedAt());
        assertEquals(customerId, workOrder.getCustomerId());
        assertEquals(vehicleId, workOrder.getVehicleId());
        assertEquals(WorkOrderStatus.RECEIVED, workOrder.getLastStatus());
        assertEquals(1, workOrder.getStatusHistory().size());
    }

    @Test
    @DisplayName("Deve iniciar o diagnóstico da ordem de serviço recebida e registrar no histórico")
    void shouldStartDiagnosis() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.RECEIVED);

        workOrder.startDiagnosis();

        assertEquals(WorkOrderStatus.IN_DIAGNOSIS, workOrder.getLastStatus());
        assertEquals(2, workOrder.getStatusHistory().size());
        assertNull(workOrder.getFinishedAt());
    }

    @ParameterizedTest
    @EnumSource(value = WorkOrderStatus.class, names = {"RECEIVED"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Não deve iniciar o diagnóstico fora da ordem de serviço recebida")
    void shouldNotStartDiagnosisOutsideReceived(WorkOrderStatus status) {
        WorkOrder workOrder = createWorkOrder(status);

        var exception = assertThrows(WorkOrderStatusTransitionInvalidException.class, workOrder::startDiagnosis);

        assertExceptionParameters(exception, workOrder.getId().toString(), status.name(), "IN_DIAGNOSIS");
        assertEquals(status, workOrder.getLastStatus());
    }

    @Test
    @DisplayName("Deve finalizar a ordem de serviço em execução e registrar a data de finalização")
    void shouldFinish() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_EXECUTION);

        workOrder.finish();

        assertEquals(WorkOrderStatus.FINISHED, workOrder.getLastStatus());
        assertNotNull(workOrder.getFinishedAt());
    }

    @ParameterizedTest
    @EnumSource(value = WorkOrderStatus.class, names = {"IN_EXECUTION"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Não deve finalizar a ordem de serviço fora da execução")
    void shouldNotFinishOutsideExecution(WorkOrderStatus status) {
        WorkOrder workOrder = createWorkOrder(status);

        var exception = assertThrows(WorkOrderStatusTransitionInvalidException.class, workOrder::finish);

        assertExceptionParameters(exception, workOrder.getId().toString(), status.name(), "FINISHED");
        assertNull(workOrder.getFinishedAt());
    }

    @Test
    @DisplayName("Deve entregar a ordem de serviço finalizada")
    void shouldDeliver() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.FINISHED);

        workOrder.deliver();

        assertEquals(WorkOrderStatus.DELIVERED, workOrder.getLastStatus());
        assertEquals(2, workOrder.getStatusHistory().size());
    }

    @ParameterizedTest
    @EnumSource(value = WorkOrderStatus.class, names = {"FINISHED"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Não deve entregar a ordem de serviço que não foi finalizada")
    void shouldNotDeliverOutsideFinished(WorkOrderStatus status) {
        WorkOrder workOrder = createWorkOrder(status);

        var exception = assertThrows(WorkOrderStatusTransitionInvalidException.class, workOrder::deliver);

        assertExceptionParameters(exception, workOrder.getId().toString(), status.name(), "DELIVERED");
    }

    @Test
    @DisplayName("Deve iniciar a execução quando o orçamento for aprovado")
    void shouldStartExecutionWhenQuotationApproved() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.AWAITING_APPROVAL);

        workOrder.approvedQuotation();

        assertEquals(WorkOrderStatus.IN_EXECUTION, workOrder.getLastStatus());
        assertEquals(2, workOrder.getStatusHistory().size());
    }

    @ParameterizedTest
    @EnumSource(value = WorkOrderStatus.class, names = {"RECEIVED", "IN_DIAGNOSIS", "AWAITING_APPROVAL"})
    @DisplayName("Deve cancelar a ordem de serviço antes da execução e registrar o evento de cancelamento")
    void shouldCancelWorkOrderBeforeExecution(WorkOrderStatus status) {
        WorkOrder workOrder = createWorkOrder(status);
        var cancelledBy = UserId.generate();

        workOrder.cancel(cancelledBy);

        assertEquals(WorkOrderStatus.CANCELLED, workOrder.getLastStatus());
        assertEquals(2, workOrder.getStatusHistory().size());
        assertNull(workOrder.getFinishedAt());

        var events = workOrder.pullDomainEvents();
        assertEquals(1, events.size());
        var event = assertInstanceOf(WorkOrderCancelledEvent.class, events.getFirst());
        assertEquals(workOrder.getId(), event.workOrderId());
        assertEquals(cancelledBy, event.cancelledBy());
    }

    @ParameterizedTest
    @EnumSource(value = WorkOrderStatus.class, names = {"RECEIVED", "IN_DIAGNOSIS", "AWAITING_APPROVAL"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Não deve cancelar a ordem de serviço a partir da execução")
    void shouldNotCancelWorkOrderFromExecution(WorkOrderStatus status) {
        WorkOrder workOrder = createWorkOrder(status);
        var cancelledBy = UserId.generate();

        var exception = assertThrows(WorkOrderStatusTransitionInvalidException.class, () -> workOrder.cancel(cancelledBy));

        assertExceptionParameters(exception, workOrder.getId().toString(), status.name(), "CANCELLED");
        assertEquals(status, workOrder.getLastStatus());
        assertTrue(workOrder.pullDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("Deve aguardar aprovação ao registrar o primeiro orçamento durante o diagnóstico")
    void shouldAwaitApprovalWhenRegisteringFirstQuotation() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_DIAGNOSIS);

        workOrder.registerQuotation();

        assertEquals(WorkOrderStatus.AWAITING_APPROVAL, workOrder.getLastStatus());
        assertEquals(2, workOrder.getStatusHistory().size());
    }

    @Test
    @DisplayName("Não deve registrar orçamento antes de a ordem de serviço entrar em diagnóstico")
    void shouldNotRegisterQuotationBeforeDiagnosis() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.RECEIVED);

        var exception = assertThrows(WorkOrderStatusTransitionInvalidException.class, workOrder::registerQuotation);

        assertExceptionParameters(exception, workOrder.getId().toString(), "RECEIVED", "AWAITING_APPROVAL");
        assertEquals(WorkOrderStatus.RECEIVED, workOrder.getLastStatus());
    }

    @ParameterizedTest
    @EnumSource(WorkOrderStatus.class)
    @DisplayName("Deve aceitar orçamento apenas em diagnóstico ou aguardando aprovação")
    void shouldAcceptQuotationOnlyInDiagnosisOrAwaitingApproval(WorkOrderStatus status) {
        WorkOrder workOrder = createWorkOrder(status);

        boolean expected = WorkOrderStatus.IN_DIAGNOSIS.equals(status) || WorkOrderStatus.AWAITING_APPROVAL.equals(status);

        assertEquals(expected, workOrder.acceptsQuotation());
    }

    @Test
    @DisplayName("Deve manter o status ao registrar um novo orçamento enquanto aguarda aprovação")
    void shouldKeepStatusWhenRegisteringAnotherQuotation() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.AWAITING_APPROVAL);

        workOrder.registerQuotation();

        assertEquals(WorkOrderStatus.AWAITING_APPROVAL, workOrder.getLastStatus());
        assertEquals(1, workOrder.getStatusHistory().size());
    }

    @Test
    @DisplayName("Não deve registrar orçamento depois que a execução começou")
    void shouldNotRegisterQuotationAfterExecutionStarted() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_EXECUTION);

        assertThrows(WorkOrderStatusTransitionInvalidException.class, workOrder::registerQuotation);
    }
}
