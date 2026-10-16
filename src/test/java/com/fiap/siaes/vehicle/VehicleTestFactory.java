package com.fiap.siaes.vehicle;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.Plate;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.infrastructure.persistence.entity.VehicleJpaEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VehicleTestFactory {

    public static final String PLATE = "ABC1D23";

    public static Vehicle createVehicle() {
        return createVehicle(CustomerId.generate());
    }

    public static Vehicle createVehicle(CustomerId customerId) {
        return Vehicle.recreate()
                .id(VehicleId.generate())
                .customerId(customerId)
                .plate(Plate.recreate(PLATE))
                .brand("Fiat")
                .model("Uno")
                .year(2020)
                .build();
    }

    public static VehicleJpaEntity createVehicleJpaEntity() {
        return VehicleJpaEntity.builder()
                .id(VehicleId.generate())
                .customerId(CustomerId.generate())
                .plate(PLATE)
                .brand("Fiat")
                .model("Uno")
                .year(2020)
                .build();
    }

}
