package com.fiap.siaes.supplies.application.usecase;

import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

public interface DeleteSuppliesUseCase {

    void execute(SuppliesId id);
}
