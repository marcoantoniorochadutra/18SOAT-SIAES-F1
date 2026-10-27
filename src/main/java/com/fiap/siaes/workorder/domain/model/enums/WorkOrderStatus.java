package com.fiap.siaes.workorder.domain.model.enums;

public enum WorkOrderStatus {
    RECEIVED, IN_DIAGNOSIS, AWAITING_APPROVAL, IN_EXECUTION, FINISHED, DELIVERED, CANCELLED;

    public boolean canChangeTo(WorkOrderStatus newStatus) {
        return switch (this) {
            case RECEIVED -> IN_DIAGNOSIS.equals(newStatus) || CANCELLED.equals(newStatus);
            case IN_DIAGNOSIS -> AWAITING_APPROVAL.equals(newStatus) || CANCELLED.equals(newStatus);
            case AWAITING_APPROVAL -> IN_EXECUTION.equals(newStatus) || CANCELLED.equals(newStatus);
            case IN_EXECUTION -> FINISHED.equals(newStatus);
            case FINISHED -> DELIVERED.equals(newStatus);
            case DELIVERED, CANCELLED -> false;
        };
    }

    public boolean acceptsQuotation() {
        return IN_DIAGNOSIS.equals(this) || AWAITING_APPROVAL.equals(this);
    }

    public boolean isFinished() {
        return FINISHED.equals(this);
    }
}
