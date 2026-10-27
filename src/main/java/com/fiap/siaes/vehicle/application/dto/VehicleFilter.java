package com.fiap.siaes.vehicle.application.dto;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.model.vo.Plate;
import lombok.Builder;

import static org.apache.commons.lang3.StringUtils.trimToNull;

public record VehicleFilter(String plate, String brand, String model, Integer year, CustomerId customerId) {

    @Builder
    public VehicleFilter {
        plate = Plate.sanitize(plate);
        brand = trimToNull(brand);
        model = trimToNull(model);
    }

    public VehicleFilter withCustomerId(CustomerId customerId) {
        return new VehicleFilter(this.plate, this.brand, this.model, this.year, customerId);
    }
}
