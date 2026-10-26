package com.fiap.siaes.vehicle.domain.repository;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;
import com.fiap.siaes.vehicle.domain.exception.VehicleNotFoundException;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.Plate;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;

public interface VehicleRepository extends RepositoryBase<Vehicle, VehicleId> {

    default Vehicle findByIdOrThrowNotFound(VehicleId id) {
        return this.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id.toString()));
    }

    boolean existsById(VehicleId id);

    default void existsByIdOrThrowNotFound(VehicleId id) {
        if (!this.existsById(id)) {
            throw new VehicleNotFoundException(id.toString());
        }
    }

    boolean existsByIdAndCustomerId(VehicleId id, CustomerId customerId);

    boolean existsByPlate(Plate plate);

    boolean existsByPlateAndIdNot(Plate plate, VehicleId id);

    void deleteById(VehicleId id);
}
