package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.usecase.CreateStockItemUseCase;
import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateStockItemAppService implements CreateStockItemUseCase {

    private final StockItemRepository stockItemRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(CreateStockItemCommand command) {
        StockItem stockItem = StockItem.builder()
                .suppliesId(command.suppliesId())
                .initialQuantity(command.initialQuantity())
                .performedBy(command.performedBy())
                .build();

        this.stockItemRepository.save(stockItem);

        this.domainEventPublisher.publish(stockItem.pullDomainEvents());
    }
}
