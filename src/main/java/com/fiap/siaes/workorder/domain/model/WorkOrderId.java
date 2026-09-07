package com.fiap.siaes.workorder.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fiap.siaes.sk.domain.UUIDWrapper;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJavaType;

import java.util.UUID;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public record WorkOrderId(UUID id) implements UUIDWrapper {

    @JsonCreator
    public WorkOrderId(String raw) {
        this(UUID.fromString(raw));
    }

    public static WorkOrderId generate() {
        return new WorkOrderId(UUID.randomUUID());
    }

    @Override
    @JsonValue
    public String toString() {
        return getIfNotNullOrDefault(this.id, UUID::toString, EMPTY);
    }

    public static class WorkOrderIdJavaType extends UUIDWrapperJavaType<WorkOrderId> {
        protected WorkOrderIdJavaType() {
            super(WorkOrderId.class, WorkOrderId::new);
        }
    }

}
