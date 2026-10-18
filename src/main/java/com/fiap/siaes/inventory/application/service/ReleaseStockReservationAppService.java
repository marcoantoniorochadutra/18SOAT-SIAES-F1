package com.fiap.siaes.inventory.application.service;

import com.fiap.siaes.inventory.application.usecase.ReleaseStockReservationUseCase;
import com.fiap.siaes.inventory.domain.model.StockItem;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReleaseStockReservationAppService implements ReleaseStockReservationUseCase {

    private final StockItemRepository stockItemRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(ReleaseStockReservationCommand command) {
        StockItem stockItem = this.stockItemRepository.findByIdOrThrowNotFound(command.suppliesId());

        stockItem.releaseReservation(command.quantity(), command.referenceId(), command.performedBy());

        this.stockItemRepository.save(stockItem);

        this.domainEventPublisher.publish(stockItem.pullDomainEvents());
    }
}
