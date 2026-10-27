package com.fiap.siaes.inventory.infrastructure.subscriber;

import com.fiap.siaes.inventory.application.usecase.CreateStockItemUseCase;
import com.fiap.siaes.inventory.application.usecase.CreateStockItemUseCase.CreateStockItemCommand;
import com.fiap.siaes.supplies.domain.event.SuppliesCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuppliesCreatedSubscriber {

    private final CreateStockItemUseCase createStockItemUseCase;

    @EventListener
    public void on(SuppliesCreatedEvent event) {
        this.createStockItemUseCase.execute(CreateStockItemCommand.of(
                event.suppliesId(),
                event.initialStock(),
                event.createdBy()));
    }
}
