package com.fiap.siaes.vehicle.infrastructure.persistence.entity;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicles")
public class VehicleJpaEntity {

    @Id
    @NotNull
    @Column(name = "id", updatable = false)
    private VehicleId id;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private CustomerId customerId;

    @NotNull
    @Column(name = "plate", nullable = false, unique = true, length = 12)
    private String plate;

    @NotNull
    @Column(name = "brand", nullable = false, length = 100)
    private String brand;

    @NotNull
    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "year", nullable = false)
    private int year;
}
