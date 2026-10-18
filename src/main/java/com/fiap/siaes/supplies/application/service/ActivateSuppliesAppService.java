package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.application.usecase.ActivateSuppliesUseCase;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivateSuppliesAppService implements ActivateSuppliesUseCase {

    private final SuppliesRepository suppliesRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(SuppliesId id) {
        Supplies supplies = this.suppliesRepository.findByIdOrThrowNotFound(id);

        supplies.activate();

        this.suppliesRepository.save(supplies);
    }
}
