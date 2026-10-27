package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.workorder.application.usecase.RegisterWorkOrderDiagnosisUseCase.RegisterWorkOrderDiagnosisCommand;
import com.fiap.siaes.workorder.domain.exception.WorkOrderDiagnosisNotAllowedException;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.user.UserTestFactory.authenticatedUserAdmin;
import static com.fiap.siaes.workorder.WorkOrderTestFactory.DIAGNOSIS;
import static com.fiap.siaes.workorder.WorkOrderTestFactory.createWorkOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Register Work Order Diagnosis")
class RegisterWorkOrderDiagnosisAppServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private RegisterWorkOrderDiagnosisAppService registerWorkOrderDiagnosisAppService;

    @Test
    @DisplayName("Deve registrar o diagnóstico com o usuário autenticado como autor")
    void shouldRegisterDiagnosis() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_DIAGNOSIS);
        AuthenticatedUser authenticatedUser = authenticatedUserAdmin();
        var command = this.command(workOrder, authenticatedUser);

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        this.registerWorkOrderDiagnosisAppService.execute(command);

        assertEquals(DIAGNOSIS, workOrder.getDiagnosis().description());
        assertEquals(authenticatedUser.id(), workOrder.getDiagnosis().diagnosedBy());
        verify(this.workOrderRepository).save(workOrder);
    }

    @Test
    @DisplayName("Não deve registrar o diagnóstico fora do diagnóstico")
    void shouldNotRegisterDiagnosisOutsideDiagnosis() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.RECEIVED);
        var command = this.command(workOrder, authenticatedUserAdmin());

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        assertThrows(WorkOrderDiagnosisNotAllowedException.class, () -> this.registerWorkOrderDiagnosisAppService.execute(command));

        verify(this.workOrderRepository, never()).save(any());
    }

    private RegisterWorkOrderDiagnosisCommand command(WorkOrder workOrder, AuthenticatedUser authenticatedUser) {
        return RegisterWorkOrderDiagnosisCommand.builder()
                .id(workOrder.getId())
                .authenticatedUser(authenticatedUser)
                .description(DIAGNOSIS)
                .build();
    }
}
