package com.fiap.siaes.maintenance.application.usecase;

import com.fiap.siaes.maintenance.domain.model.Maintenance;

import java.util.List;

public interface ListMaintenancesUseCase {

    List<Maintenance> execute();
}
