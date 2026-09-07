package com.fiap.siaes.workorder.infrastructure.persistence.mapper;

import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.infrastructure.persistence.entity.WorkOrderJpa;

public final class WorkOrderMapper {

    private WorkOrderMapper() {
    }

    public static WorkOrderJpa toEntity(WorkOrder workOrder) {
        if (workOrder == null) {
            return null;
        }
        return new WorkOrderJpa(workOrder.getId());
    }

    public static WorkOrder toDomain(WorkOrderJpa entity) {
        if (entity == null) {
            return null;
        }

        return WorkOrder.builder()
                .id(entity.getId())
                .build();
    }
}
