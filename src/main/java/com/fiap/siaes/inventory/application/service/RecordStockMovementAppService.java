package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.usecase.RecordStockMovementUseCase;
import com.fiap.siaes.inventory.domain.model.StockMovement;
import com.fiap.siaes.inventory.domain.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordStockMovementAppService implements RecordStockMovementUseCase {

    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(RecordStockMovementCommand command) {
        this.stockMovementRepository.save(StockMovement.from(command.stockMovedEvent()));
    }
}
