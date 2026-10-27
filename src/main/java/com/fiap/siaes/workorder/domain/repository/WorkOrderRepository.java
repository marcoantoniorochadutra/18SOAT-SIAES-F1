package com.fiap.siaes.workorder.domain.repository;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import com.fiap.siaes.workorder.domain.exception.WorkOrderNotFoundException;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

public interface WorkOrderRepository extends RepositoryBase<WorkOrder, WorkOrderId> {

    default WorkOrder findByIdOrThrowNotFound(WorkOrderId id) {
        return this.findById(id)
                .orElseThrow(() -> new WorkOrderNotFoundException(id.toString()));
    }

    boolean existsById(WorkOrderId id);

    default void existsByIdOrThrowNotFound(WorkOrderId id) {
        if (!this.existsById(id)) {
            throw new WorkOrderNotFoundException(id.toString());
        }
    }

    void deleteAllByCustomerId(CustomerId customerId);
}
