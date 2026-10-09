package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.application.usecase.CreateSuppliesUseCase;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateSuppliesAppService implements CreateSuppliesUseCase {

    private final SuppliesRepository suppliesRepository;

    @Override
    @Transactional
    public SuppliesId execute(CreateSuppliesCommand command) {
        Supplies supplies = Supplies.register(command.description(), command.unitPrice(), command.initialStock());
        return this.suppliesRepository.save(supplies).getId();
    }
}
