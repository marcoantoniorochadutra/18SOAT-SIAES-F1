package com.fiap.siaes.vehicle.infrastructure.persistence.adapter;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.VehicleId;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VehicleRepositoryAdapter implements VehicleRepository {

//    private final VehicleJpaRepository vehicleJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Vehicle save(Vehicle vehicle) {
//        var entity = VehicleMapper.toEntity(vehicle);
//        var saved = this.vehicleJpaRepository.save(entity);
//        return VehicleMapper.toDomain(saved);
        return null;
    }

    @Override
    public Optional<Vehicle> findById(VehicleId id) {
//        return this.vehicleJpaRepository.findById(id).map(VehicleMapper::toDomain);
        return null;
    }

    @Override
    public List<Vehicle> findAll() {
//        return this.vehicleJpaRepository.findAll().stream().map(VehicleMapper::toDomain).toList();
        return null;
    }

    @Override
    public List<Vehicle> findByOwnerId(CustomerId ownerId) {
//        return this.vehicleJpaRepository.findByOwnerId(ownerId).stream().map(VehicleMapper::toDomain).toList();
        return null;
    }

    @Override
    public boolean existsByPlate(String plate) {
//        return this.vehicleJpaRepository.existsByPlate(plate);
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(VehicleId id) {
//        this.vehicleJpaRepository.deleteById(id);
    }
}
