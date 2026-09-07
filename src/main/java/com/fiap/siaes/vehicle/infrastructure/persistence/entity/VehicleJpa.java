package com.fiap.siaes.vehicle.infrastructure.persistence.entity;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.vehicle.domain.model.VehicleId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleJpa {

    @Id
    private VehicleId id;

    private CustomerId ownerId;
    private String plate;
    private String brand;
    private String model;
    private int year;
}
