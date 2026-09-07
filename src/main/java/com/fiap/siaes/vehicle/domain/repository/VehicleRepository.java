package com.fiap.siaes.vehicle.domain.repository;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.VehicleId;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {

    Vehicle save(Vehicle vehicle);

    Optional<Vehicle> findById(VehicleId id);

    List<Vehicle> findAll();

    List<Vehicle> findByOwnerId(CustomerId ownerId);

    boolean existsByPlate(String plate);

    void deleteById(VehicleId id);
}
