package com.fiap.siaes.maintenance.infrastructure.persistence.adapter;

import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.MaintenanceId;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MaintenanceRepositoryAdapter implements MaintenanceRepository {

//    private final MaintenanceJpaRepository maintenanceJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Maintenance save(Maintenance maintenance) {
//        var entity = MaintenanceMapper.toEntity(maintenance);
//        var saved = this.maintenanceJpaRepository.save(entity);
//        return MaintenanceMapper.toDomain(saved);
        return null;
    }

    @Override
    public Optional<Maintenance> findById(MaintenanceId id) {
//        return this.maintenanceJpaRepository.findById(id).map(MaintenanceMapper::toDomain);
        return null;
    }

    @Override
    public List<Maintenance> findAll() {
//        return this.maintenanceJpaRepository.findAll().stream().map(MaintenanceMapper::toDomain).toList();
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(MaintenanceId id) {
//        this.maintenanceJpaRepository.deleteById(id);
    }
}
