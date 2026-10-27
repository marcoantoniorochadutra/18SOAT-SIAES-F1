package com.fiap.siaes.workorder.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.domain.model.AggregateRoot;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.event.WorkOrderCancelledEvent;
import com.fiap.siaes.workorder.domain.exception.WorkOrderStatusTransitionInvalidException;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

@Getter
public class WorkOrder extends AggregateRoot {

    private WorkOrderId id;
    private CustomerId customerId;
    private VehicleId vehicleId;

    private WorkOrderStatus lastStatus;
    private Set<WorkOrderStatusHistory> statusHistory;

    private Instant createdAt;
    private Instant finishedAt;
    private Long version;

    @Builder
    public WorkOrder(CustomerId customerId, VehicleId vehicleId) {
        this.id = WorkOrderId.generate();
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.createdAt = Instant.now();
        this.addStatus(WorkOrderStatus.RECEIVED);
    }

    @Builder(builderMethodName = "recreate", builderClassName = "RecreateBuilder")
    public WorkOrder(WorkOrderId id, CustomerId customerId, VehicleId vehicleId, WorkOrderStatus lastStatus,
                     Set<WorkOrderStatusHistory> statusHistory, Instant createdAt, Instant finishedAt, Long version) {
        this.id = id;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.lastStatus = lastStatus;
        this.statusHistory = statusHistory;
        this.createdAt = createdAt;
        this.finishedAt = finishedAt;
        this.version = version;
    }

    public boolean acceptsQuotation() {
        return this.lastStatus.acceptsQuotation();
    }

    public void startDiagnosis() {
        this.transitionTo(WorkOrderStatus.IN_DIAGNOSIS);
    }

    public void registerQuotation() {
        if (WorkOrderStatus.AWAITING_APPROVAL.equals(this.lastStatus))
            return;

        this.transitionTo(WorkOrderStatus.AWAITING_APPROVAL);
    }

    public void approvedQuotation() {
        this.transitionTo(WorkOrderStatus.IN_EXECUTION);
    }

    public void finish() {
        this.transitionTo(WorkOrderStatus.FINISHED);
    }

    public void deliver() {
        this.transitionTo(WorkOrderStatus.DELIVERED);
    }

    public void cancel(UserId cancelledBy) {
        this.transitionTo(WorkOrderStatus.CANCELLED);

        this.registerEvent(WorkOrderCancelledEvent.of(this.id, cancelledBy, Instant.now()));
    }

    private void transitionTo(WorkOrderStatus newStatus) {
        if (!this.lastStatus.canChangeTo(newStatus))
            throw new WorkOrderStatusTransitionInvalidException(this.id.toString(), this.lastStatus.name(), newStatus.name());

        this.addStatus(newStatus);

        if (newStatus.isFinished())
            this.finishedAt = Instant.now();
    }

    private void addStatus(WorkOrderStatus newStatus) {
        this.lastStatus = newStatus;

        if (isNull(this.statusHistory))
            this.statusHistory = new HashSet<>();

        this.statusHistory.add(WorkOrderStatusHistory.of(newStatus));
    }

    public Set<WorkOrderStatusHistory> getStatusHistory() {
        return isNull(this.statusHistory) ? Set.of() : Collections.unmodifiableSet(this.statusHistory);
    }
}
