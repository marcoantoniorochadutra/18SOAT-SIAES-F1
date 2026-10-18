package com.fiap.siaes.supplies.application.usecase;

import com.fiap.siaes.supplies.application.dto.SuppliesResponse;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;

public interface GetSuppliesByIdUseCase {

    SuppliesResponse execute(SuppliesId id);
}
