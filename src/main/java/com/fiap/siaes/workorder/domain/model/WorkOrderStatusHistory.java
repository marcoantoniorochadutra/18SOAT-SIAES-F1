package com.fiap.siaes.workorder.domain.model;

import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderStatusHistoryId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class WorkOrderStatusHistory {

    private WorkOrderStatusHistoryId id;
    private WorkOrderStatus status;
    private Instant updatedAt;

    public static WorkOrderStatusHistory of(WorkOrderStatus status) {
        return new WorkOrderStatusHistory(WorkOrderStatusHistoryId.generate(), status, Instant.now());
    }
}
