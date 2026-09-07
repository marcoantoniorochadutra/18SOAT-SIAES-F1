package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.application.usecase.DeleteSuppliesUseCase;
import com.fiap.siaes.supplies.domain.model.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteSuppliesAppService implements DeleteSuppliesUseCase {

    private final SuppliesRepository suppliesRepository;

    @Override
    @Transactional
    public void execute(SuppliesId id) {
        this.suppliesRepository.deleteById(id);
    }
}
