package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.application.usecase.UpdateSuppliesUseCase;
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
public class UpdateSuppliesAppService implements UpdateSuppliesUseCase {

    private final SuppliesRepository suppliesRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SuppliesId execute(UpdateSuppliesCommand command) {
        Supplies supplies = this.suppliesRepository.findByIdOrThrowNotFound(command.id());

        supplies.update(command.sku(), command.description(), command.unitPrice());

        this.validateSupplies(supplies);

        this.suppliesRepository.save(supplies);

        return supplies.getId();
    }

    private void validateSupplies(Supplies supplies) {
        if (this.suppliesRepository.existsBySkuAndIdNot(supplies.getSku(), supplies.getId())) {
            throw new SuppliesSkuAlreadyExistsException(supplies.getSku().getValue());
        }
    }
}
