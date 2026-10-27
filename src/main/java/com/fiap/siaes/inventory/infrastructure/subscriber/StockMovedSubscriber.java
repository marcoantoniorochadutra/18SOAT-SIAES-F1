package com.fiap.siaes.inventory.infrastructure.subscriber;

import com.fiap.siaes.inventory.application.usecase.RecordStockMovementUseCase;
import com.fiap.siaes.inventory.application.usecase.RecordStockMovementUseCase.RecordStockMovementCommand;
import com.fiap.siaes.inventory.domain.event.StockMovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockMovedSubscriber {

    private final RecordStockMovementUseCase recordStockMovementUseCase;

    @EventListener
    public void on(StockMovedEvent event) {
        this.recordStockMovementUseCase.execute(RecordStockMovementCommand.of(event));
    }
}
