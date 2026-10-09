package com.fiap.siaes.vehicle.infrastructure.persistence.entity;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Entity
//@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleJpa {

    private VehicleId id;

    private CustomerId ownerId;
    private String plate;
    private String brand;
    private String model;
    private int year;
}
