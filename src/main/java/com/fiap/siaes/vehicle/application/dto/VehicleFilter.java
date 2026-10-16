package com.fiap.siaes.vehicle.application.dto;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import lombok.Builder;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

public record VehicleFilter(String plate, String brand, String model, Integer year, CustomerId customerId) {

    @Builder
    public VehicleFilter {
        plate = isBlank(plate) ? null : plate.toUpperCase().replaceAll("[^A-Z0-9]", "");
        brand = trimToNull(brand);
        model = trimToNull(model);
    }
}
