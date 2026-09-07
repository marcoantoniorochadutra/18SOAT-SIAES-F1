package com.fiap.siaes.supplies.application.usecase;

import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.SuppliesId;

import java.math.BigDecimal;

public interface UpdateSuppliesUseCase {

    Supplies execute(SuppliesId id, UpdateSuppliesCommand command);

    record UpdateSuppliesCommand(String description, BigDecimal unitPrice) {}
}
