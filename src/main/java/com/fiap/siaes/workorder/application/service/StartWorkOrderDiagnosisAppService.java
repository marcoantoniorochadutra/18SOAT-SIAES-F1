package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.workorder.application.usecase.StartWorkOrderDiagnosisUseCase;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StartWorkOrderDiagnosisAppService implements StartWorkOrderDiagnosisUseCase {

    private final WorkOrderRepository workOrderRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(StartWorkOrderDiagnosisCommand command) {
        WorkOrder workOrder = this.workOrderRepository.findByIdOrThrowNotFound(command.id());

        workOrder.startDiagnosis();

        this.workOrderRepository.save(workOrder);
    }
}
