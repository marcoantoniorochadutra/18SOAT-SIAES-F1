package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.dto.StockMovementResponse;
import com.fiap.siaes.inventory.application.usecase.ListStockMovementsUseCase;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.inventory.domain.repository.StockMovementRepository;
import com.fiap.siaes.sk.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListStockMovementsAppService implements ListStockMovementsUseCase {

    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public PageResponse<StockMovementResponse> execute(ListStockMovementsCommand command) {
        this.stockItemRepository.existsByIdOrThrowNotFound(command.suppliesId());

        return this.stockMovementRepository.findAllBySuppliesId(command.suppliesId(), command.pageQuery());
    }
}
