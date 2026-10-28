package com.fiap.siaes.workorder.infrastructure.persistence.projection;

import java.time.Instant;
import java.util.UUID;

public interface WorkOrderStatusHistoryProjection {
    UUID getId();

    UUID getWorkOrderId();

    String getStatus();

    Instant getUpdatedAt();
}
