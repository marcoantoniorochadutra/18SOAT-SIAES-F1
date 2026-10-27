package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.workorder.application.usecase.DeleteCustomerWorkOrdersUseCase;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCustomerWorkOrdersAppService implements DeleteCustomerWorkOrdersUseCase {

    private final WorkOrderRepository workOrderRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(CustomerId customerId) {
        this.workOrderRepository.deleteAllByCustomerId(customerId);
    }
}
