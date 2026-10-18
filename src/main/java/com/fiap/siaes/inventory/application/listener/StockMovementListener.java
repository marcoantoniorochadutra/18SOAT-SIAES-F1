package com.fiap.siaes.inventory.application.listener;

import com.fiap.siaes.inventory.domain.event.StockMovedEvent;
import com.fiap.siaes.inventory.domain.model.StockMovement;
import com.fiap.siaes.inventory.domain.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.fiap.siaes.sk.infraestructure.config.AsyncConfig.DOMAIN_EVENT_EXECUTOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockMovementListener {

    private final StockMovementRepository stockMovementRepository;

    @Async(DOMAIN_EVENT_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(StockMovedEvent event) {
        try {
            this.stockMovementRepository.save(StockMovement.from(event));
        } catch (RuntimeException exception) {
            log.error("Failed to record stock movement {} for supplies {}", event.type(), event.suppliesId(), exception);
        }
    }
}
