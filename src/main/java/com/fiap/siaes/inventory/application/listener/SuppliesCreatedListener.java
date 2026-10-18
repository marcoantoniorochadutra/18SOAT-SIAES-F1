package com.fiap.siaes.inventory.application.listener;

import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import com.fiap.siaes.supplies.domain.event.SuppliesCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuppliesCreatedListener {

    private final StockItemRepository stockItemRepository;
    private final DomainEventPublisher domainEventPublisher;

    @EventListener
    public void on(SuppliesCreatedEvent event) {
        StockItem stockItem = StockItem.builder()
                .suppliesId(event.suppliesId())
                .initialQuantity(event.initialStock())
                .performedBy(event.createdBy())
                .build();

        this.stockItemRepository.save(stockItem);

        this.domainEventPublisher.publish(stockItem.pullDomainEvents());
    }
}
