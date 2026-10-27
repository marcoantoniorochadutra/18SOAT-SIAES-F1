package com.fiap.siaes.vehicle.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.exception.InvalidVehicleYearException;
import com.fiap.siaes.vehicle.domain.exception.VehicleBrandRequiredException;
import com.fiap.siaes.vehicle.domain.exception.VehicleModelRequiredException;
import com.fiap.siaes.vehicle.domain.model.vo.Plate;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import lombok.Builder;
import lombok.Getter;

import java.time.Year;
import java.time.ZoneId;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
public class Vehicle {

    private final VehicleId id;
    private final CustomerId customerId;
    private Plate plate;
    private String brand;
    private String model;
    private int year;

    @Builder
    public Vehicle(CustomerId customerId, String plate, String brand, String model, int year) {
        this.validateVehicle(brand, model, year);

        this.id = VehicleId.generate();
        this.customerId = customerId;
        this.plate = Plate.create(plate);
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    @Builder(builderMethodName = "recreate", builderClassName = "RecreateBuilder")
    public Vehicle(VehicleId id, CustomerId customerId, Plate plate, String brand, String model, int year) {
        this.id = id;
        this.customerId = customerId;
        this.plate = plate;
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public void update(String plate, String brand, String model, int year) {
        this.validateVehicle(brand, model, year);

        this.plate = Plate.create(plate);
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public boolean isOwnedBy(CustomerId customerId) {
        return this.customerId.equals(customerId);
    }

    private void validateVehicle(String brand, String model, int year) {
        if (isBlank(brand)) {
            throw new VehicleBrandRequiredException();
        }

        if (isBlank(model)) {
            throw new VehicleModelRequiredException();
        }

        this.validateVehicleYear(year);
    }

    private void validateVehicleYear(int year) {
        if (year > Year.now(ZoneId.systemDefault()).getValue() + 1) {
            throw new InvalidVehicleYearException(year);
        }
    }
}
