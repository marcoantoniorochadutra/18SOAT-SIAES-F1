package com.fiap.siaes.workorder.infrastructure.persistence.projection;

import java.time.Instant;
import java.util.UUID;

public interface ListWorkOrderProjection {
    UUID getId();

    UUID getCustomerId();

    String getCustomerName();

    UUID getVehicleId();

    String getVehiclePlate();

    String getVehicleBrand();

    String getVehicleModel();

    String getStatus();

    Instant getCreatedAt();

    Instant getFinishedAt();
}
