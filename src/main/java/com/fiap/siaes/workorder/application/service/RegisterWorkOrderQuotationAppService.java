package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.workorder.application.usecase.RegisterWorkOrderQuotationUseCase;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterWorkOrderQuotationAppService implements RegisterWorkOrderQuotationUseCase {

    private final WorkOrderRepository workOrderRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(RegisterWorkOrderQuotationCommand command) {
        WorkOrder workOrder = this.workOrderRepository.findByIdOrThrowNotFound(command.workOrderId());

        workOrder.registerQuotation();

        this.workOrderRepository.save(workOrder);
    }
}
