package com.fiap.siaes.inventory.infrastructure.subscriber;

import com.fiap.siaes.inventory.application.usecase.RecordStockMovementUseCase;
import com.fiap.siaes.inventory.application.usecase.RecordStockMovementUseCase.RecordStockMovementCommand;
import com.fiap.siaes.inventory.domain.event.StockMovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.fiap.siaes.sk.infrastructure.config.AsyncConfig.DOMAIN_EVENT_EXECUTOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockMovedSubscriber {

    private final RecordStockMovementUseCase recordStockMovementUseCase;

    @Async(DOMAIN_EVENT_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(StockMovedEvent event) {
        try {
            this.recordStockMovementUseCase.execute(RecordStockMovementCommand.of(event));
        } catch (RuntimeException exception) {
            log.error("Failed to record stock movement {} for supplies {}", event.type(), event.suppliesId(), exception);
        }
    }
}
