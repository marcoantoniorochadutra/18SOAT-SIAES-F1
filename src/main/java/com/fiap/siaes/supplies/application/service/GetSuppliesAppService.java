package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.application.usecase.GetSuppliesUseCase;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSuppliesAppService implements GetSuppliesUseCase {

    private final SuppliesRepository suppliesRepository;

    @Override
    public Supplies execute(SuppliesId id) {
        return this.suppliesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Peça/insumo não encontrado: " + id));
    }
}
