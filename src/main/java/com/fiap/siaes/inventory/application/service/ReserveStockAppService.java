package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.usecase.ReserveStockUseCase;
import com.fiap.siaes.inventory.domain.exception.InactiveSuppliesException;
import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReserveStockAppService implements ReserveStockUseCase {

    private final StockItemRepository stockItemRepository;
    private final SuppliesRepository suppliesRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(ReserveStockCommand command) {
        StockItem stockItem = this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId());

        this.validateActive(stockItem);

        stockItem.reserve(command.quantity(), command.referenceId(), command.performedBy());

        this.stockItemRepository.save(stockItem);

        this.domainEventPublisher.publish(stockItem.pullDomainEvents());
    }

    private void validateActive(StockItem stockItem) {
        if (!this.suppliesRepository.findByIdOrThrowNotFound(stockItem.getSuppliesId()).isActive()) {
            throw new InactiveSuppliesException(stockItem.getSuppliesId().toString());
        }
    }
}
