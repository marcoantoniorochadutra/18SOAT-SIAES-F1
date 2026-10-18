package com.fiap.siaes.maintenance.infrastructure.persistence.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface ListMaintenanceProjection {
    UUID getId();

    String getCode();

    String getDescription();

    BigDecimal getPrice();

    int getEstimatedDurationMinutes();

    boolean isActive();
}
