package com.fiap.siaes.vehicle.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.exception.InvalidVehicleYearException;
import com.fiap.siaes.vehicle.domain.model.vo.Plate;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;
import java.time.ZoneId;

@Getter
@Setter
@NoArgsConstructor
public class Vehicle {

    private VehicleId id;
    private CustomerId customerId;
    private Plate plate;
    private String brand;
    private String model;
    private int year;

    @Builder
    public Vehicle(CustomerId customerId, String plate, String brand, String model, int year) {
        this.validateVehicleYear(year);

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
        this.validateVehicleYear(year);

        this.plate = Plate.create(plate);
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public boolean isOwnedBy(CustomerId customerId) {
        return this.customerId.equals(customerId);
    }

    private void validateVehicleYear(int year) {
        if (year > Year.now(ZoneId.systemDefault()).getValue() + 1) {
            throw new InvalidVehicleYearException(year);
        }
    }
}
