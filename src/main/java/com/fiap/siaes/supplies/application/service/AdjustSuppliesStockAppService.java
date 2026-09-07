package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.application.usecase.AdjustSuppliesStockUseCase;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdjustSuppliesStockAppService implements AdjustSuppliesStockUseCase {

    private final SuppliesRepository suppliesRepository;

    @Override
    @Transactional
    public Supplies execute(SuppliesId id, AdjustSuppliesStockCommand command) {
        Supplies supplies = this.suppliesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Peça/insumo não encontrado: " + id));

        if (command.quantity() > 0) {
            supplies.increaseStock(command.quantity());
        } else if (command.quantity() < 0) {
            supplies.decreaseStock(-command.quantity());
        }

        return this.suppliesRepository.save(supplies);
    }
}
