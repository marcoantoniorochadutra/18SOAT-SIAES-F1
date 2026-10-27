package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import com.fiap.siaes.workorder.application.port.WorkOrderQuotationPort;
import com.fiap.siaes.workorder.application.usecase.CancelWorkOrderUseCase;
import com.fiap.siaes.workorder.domain.exception.WorkOrderOpenQuotationException;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelWorkOrderAppService implements CancelWorkOrderUseCase {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderQuotationPort workOrderQuotationPort;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(CancelWorkOrderCommand command) {
        WorkOrder workOrder = this.workOrderRepository.findByIdOrThrowNotFound(command.id());

        if (this.workOrderQuotationPort.hasQuotationAwaitingApproval(command.id()))
            throw new WorkOrderOpenQuotationException(command.id().toString());

        workOrder.cancel(command.authenticatedUser().id());

        this.workOrderRepository.save(workOrder);

        this.domainEventPublisher.publish(workOrder.pullDomainEvents());
    }
}
