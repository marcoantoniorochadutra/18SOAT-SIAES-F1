package com.fiap.siaes.quotation.infrastructure.persistence.projection;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface ListQuotationProjection {
    UUID getId();

    UUID getWorkOrderId();

    UUID getCustomerId();

    String getCustomerName();

    UUID getVehicleId();

    String getVehiclePlate();

    String getVehicleBrand();

    String getVehicleModel();

    String getDescription();

    String getStatus();

    BigDecimal getTotalAmount();

    Instant getCreatedAt();
}
