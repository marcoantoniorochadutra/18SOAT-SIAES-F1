package com.fiap.siaes.supplies.infrastructure.persistence.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface ListSuppliesProjection {
    UUID getId();

    String getSku();

    String getDescription();

    BigDecimal getUnitPrice();

    boolean isActive();
}
