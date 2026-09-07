package com.fiap.siaes.supplies.application.usecase;

import com.fiap.siaes.supplies.domain.model.SuppliesId;

import java.math.BigDecimal;

public interface CreateSuppliesUseCase {

    SuppliesId execute(CreateSuppliesCommand command);

    record CreateSuppliesCommand(String description, BigDecimal unitPrice, int initialStock) {}
}
