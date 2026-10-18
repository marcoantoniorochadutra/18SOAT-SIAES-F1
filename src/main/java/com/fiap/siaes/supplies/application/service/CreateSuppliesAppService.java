package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import com.fiap.siaes.supplies.application.usecase.CreateSuppliesUseCase;
import com.fiap.siaes.supplies.domain.exception.SuppliesSkuAlreadyExistsException;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateSuppliesAppService implements CreateSuppliesUseCase {

    private final SuppliesRepository suppliesRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SuppliesId execute(CreateSuppliesCommand command) {
        Supplies supplies = this.createSupplies(command);

        this.validateSupplies(supplies);

        this.suppliesRepository.save(supplies);

        this.domainEventPublisher.publish(supplies.pullDomainEvents());

        return supplies.getId();
    }

    private void validateSupplies(Supplies supplies) {
        if (this.suppliesRepository.existsBySku(supplies.getSku())) {
            throw new SuppliesSkuAlreadyExistsException(supplies.getSku().getValue());
        }
    }

    private Supplies createSupplies(CreateSuppliesCommand command) {
        return Supplies.builder()
                .sku(command.sku())
                .description(command.description())
                .unitPrice(command.unitPrice())
                .initialStock(command.initialStock())
                .createdBy(command.authenticatedUser().id())
                .build();
    }
}
