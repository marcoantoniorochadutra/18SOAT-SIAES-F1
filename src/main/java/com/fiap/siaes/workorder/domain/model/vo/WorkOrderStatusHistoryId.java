package com.fiap.siaes.workorder.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infrastructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

public record WorkOrderStatusHistoryId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public WorkOrderStatusHistoryId(String raw) {
        this(UUID.fromString(raw));
    }

    public static WorkOrderStatusHistoryId generate() {
        return new WorkOrderStatusHistoryId(UUID.randomUUID());
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, "");
    }

    public static class WorkOrderStatusHistoryIdJavaType extends UUIDWrapperJavaType<WorkOrderStatusHistoryId> {
        protected WorkOrderStatusHistoryIdJavaType() {
            super(WorkOrderStatusHistoryId.class, WorkOrderStatusHistoryId::new);
        }
    }

}
