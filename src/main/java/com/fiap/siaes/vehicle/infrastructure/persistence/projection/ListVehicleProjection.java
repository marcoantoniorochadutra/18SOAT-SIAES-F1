package com.fiap.siaes.vehicle.infrastructure.persistence.projection;

import java.util.UUID;

public interface ListVehicleProjection {
    UUID getId();

    String getPlate();

    String getModel();

    int getYear();

    UUID getCustomerId();

    String getCustomerName();
}
