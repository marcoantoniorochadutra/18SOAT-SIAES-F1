package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.workorder.application.usecase.RegisterWorkOrderDiagnosisUseCase;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterWorkOrderDiagnosisAppService implements RegisterWorkOrderDiagnosisUseCase {

    private final WorkOrderRepository workOrderRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(RegisterWorkOrderDiagnosisCommand command) {
        WorkOrder workOrder = this.workOrderRepository.findByIdOrThrowNotFound(command.id());

        workOrder.registerDiagnosis(command.description(), command.authenticatedUser().id());

        this.workOrderRepository.save(workOrder);
    }
}
