package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.application.usecase.UpdateSuppliesUseCase;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateSuppliesAppService implements UpdateSuppliesUseCase {

    private final SuppliesRepository suppliesRepository;

    @Override
    @Transactional
    public Supplies execute(SuppliesId id, UpdateSuppliesCommand command) {
        Supplies supplies = this.suppliesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Peça/insumo não encontrado: " + id));

        supplies.setDescription(command.description());
        supplies.setUnitPrice(command.unitPrice());

        return this.suppliesRepository.save(supplies);
    }
}
