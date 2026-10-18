package com.fiap.siaes.maintenance.infrastructure.persistence.adapter;

import com.fiap.siaes.sk.pagination.PageQuery;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.maintenance.application.dto.MaintenanceFilter;
import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;
import com.fiap.siaes.maintenance.application.port.MaintenanceQueryPort;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceCode;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import com.fiap.siaes.maintenance.infrastructure.persistence.entity.MaintenanceJpaEntity;
import com.fiap.siaes.maintenance.infrastructure.persistence.mapper.MaintenanceMapper;
import com.fiap.siaes.maintenance.infrastructure.persistence.repository.MaintenanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MaintenanceRepositoryAdapter implements MaintenanceRepository, MaintenanceQueryPort {

    private final MaintenanceJpaRepository maintenanceJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Maintenance save(Maintenance maintenance) {
        MaintenanceJpaEntity entity = MaintenanceMapper.toEntity(maintenance);

        MaintenanceJpaEntity saved = this.maintenanceJpaRepository.save(entity);

        return MaintenanceMapper.toDomain(saved);
    }

    @Override
    public Optional<Maintenance> findById(MaintenanceId id) {
        return this.maintenanceJpaRepository.findById(id)
                .map(MaintenanceMapper::toDomain);
    }

    @Override
    public boolean existsByCode(MaintenanceCode code) {
        return this.maintenanceJpaRepository.existsByCode(code.getValue());
    }

    @Override
    public boolean existsByCodeAndIdNot(MaintenanceCode code, MaintenanceId id) {
        return this.maintenanceJpaRepository.existsByCodeAndIdNot(code.getValue(), id);
    }

    @Override
    public PageResponse<MaintenanceResponse> findAllByFilter(MaintenanceFilter filter, PageQuery pageQuery) {
        Slice<MaintenanceResponse> results = this.maintenanceJpaRepository.findAllMaintenances(filter.code(), filter.description(), filter.active(),
                                                                                      PageRequest.of(pageQuery.page(), pageQuery.size()))
                .map(MaintenanceMapper::toResponse);

        return PageResponse.from(results);
    }
}
