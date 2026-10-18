package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.dto.StockItemResponse;
import com.fiap.siaes.inventory.application.usecase.GetStockItemUseCase;
import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetStockItemAppService implements GetStockItemUseCase {

    private final StockItemRepository stockItemRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public StockItemResponse execute(SuppliesId suppliesId) {
        StockItem stockItem = this.stockItemRepository.findByIdOrThrowNotFound(suppliesId);

        return StockItemResponse.from(stockItem);
    }
}
