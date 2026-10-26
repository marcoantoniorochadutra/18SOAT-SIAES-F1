package com.fiap.siaes.inventory.application.usecase;

import com.fiap.siaes.inventory.domain.event.StockMovedEvent;

public interface RecordStockMovementUseCase {

    void execute(RecordStockMovementCommand command);

    record RecordStockMovementCommand(StockMovedEvent stockMovedEvent) {

        public static RecordStockMovementCommand of(StockMovedEvent stockMovedEvent) {
            return new RecordStockMovementCommand(stockMovedEvent);
        }
    }
}
