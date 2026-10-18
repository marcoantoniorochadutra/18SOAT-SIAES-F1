package com.fiap.siaes.vehicle.infrastructure.persistence.adapter;

import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.vehicle.application.dto.VehicleFilter;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.vehicle.application.port.VehicleQueryPort;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.Plate;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import com.fiap.siaes.vehicle.infrastructure.persistence.entity.VehicleJpaEntity;
import com.fiap.siaes.vehicle.infrastructure.persistence.mapper.VehicleMapper;
import com.fiap.siaes.vehicle.infrastructure.persistence.repository.VehicleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VehicleRepositoryAdapter implements VehicleRepository, VehicleQueryPort {

    private final VehicleJpaRepository vehicleJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Vehicle save(Vehicle vehicle) {
        VehicleJpaEntity entity = VehicleMapper.toEntity(vehicle);

        VehicleJpaEntity saved = this.vehicleJpaRepository.save(entity);

        return VehicleMapper.toDomain(saved);
    }

    @Override
    public Optional<Vehicle> findById(VehicleId id) {
        return this.vehicleJpaRepository.findById(id).map(VehicleMapper::toDomain);
    }

    @Override
    public boolean existsById(VehicleId id) {
        return this.vehicleJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByPlate(Plate plate) {
        return this.vehicleJpaRepository.existsByPlate(plate.getValue());
    }

    @Override
    public boolean existsByPlateAndIdNot(Plate plate, VehicleId id) {
        return this.vehicleJpaRepository.existsByPlateAndIdNot(plate.getValue(), id);
    }

    @Override
    public PageResponse<VehicleResponse> findAllByFilter(VehicleFilter filter, PageQuery pageQuery) {
        Slice<VehicleResponse> results = this.vehicleJpaRepository.findAllVehicles(filter.plate(), filter.brand(), filter.model(),
                                                                                        filter.year(), filter.customerId(),
                                                                                        PageRequest.of(pageQuery.page(), pageQuery.size()))
                .map(VehicleMapper::toResponse);

        return PageResponse.from(results);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(VehicleId id) {
        this.vehicleJpaRepository.deleteById(id);
    }
}
