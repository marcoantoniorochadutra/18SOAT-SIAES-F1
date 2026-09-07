package com.fiap.siaes.supplies.application.usecase;

import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.SuppliesId;

public interface GetSuppliesUseCase {

    Supplies execute(SuppliesId id);
}
