package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.usecase.MoveStockUseCase;
import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MoveStockAppService implements MoveStockUseCase {

    private final StockItemRepository stockItemRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SuppliesId execute(MoveStockCommand command) {
        StockItem stockItem = this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId());

        stockItem.moveStock(command.type(), command.quantity(), command.reason(), command.authenticatedUser().id());

        this.stockItemRepository.save(stockItem);

        this.domainEventPublisher.publish(stockItem.pullDomainEvents());

        return stockItem.getSuppliesId();
    }
}
