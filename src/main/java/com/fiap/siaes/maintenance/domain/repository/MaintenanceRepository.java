package com.fiap.siaes.maintenance.domain.repository;

import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.MaintenanceId;

import java.util.List;
import java.util.Optional;

public interface MaintenanceRepository {

    Maintenance save(Maintenance maintenance);

    Optional<Maintenance> findById(MaintenanceId id);

    List<Maintenance> findAll();

    void deleteById(MaintenanceId id);
}
