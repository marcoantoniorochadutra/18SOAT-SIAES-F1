package com.fiap.siaes.vehicle.domain.repository;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;

import java.util.List;

public interface VehicleRepository extends RepositoryBase<Vehicle, VehicleId> {

    List<Vehicle> findAll();

    List<Vehicle> findByOwnerId(CustomerId ownerId);

    boolean existsByPlate(String plate);

    void deleteById(VehicleId id);
}
