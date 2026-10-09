package com.fiap.siaes.maintenance.domain.repository;

import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.sk.domain.repository.RepositoryBase;

import java.util.List;

public interface MaintenanceRepository extends RepositoryBase<Maintenance, MaintenanceId> {

    List<Maintenance> findAll();

    void deleteById(MaintenanceId id);
}
