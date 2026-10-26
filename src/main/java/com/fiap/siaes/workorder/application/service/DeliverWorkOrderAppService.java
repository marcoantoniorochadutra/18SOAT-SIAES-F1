package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.workorder.application.usecase.DeliverWorkOrderUseCase;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliverWorkOrderAppService implements DeliverWorkOrderUseCase {

    private final WorkOrderRepository workOrderRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(DeliverWorkOrderCommand command) {
        WorkOrder workOrder = this.workOrderRepository.findByIdOrThrowNotFound(command.id());

        workOrder.deliver();

        this.workOrderRepository.save(workOrder);
    }
}
