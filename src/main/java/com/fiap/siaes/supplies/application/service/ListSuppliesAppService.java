package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.application.usecase.ListSuppliesUseCase;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListSuppliesAppService implements ListSuppliesUseCase {

    private final SuppliesRepository suppliesRepository;

    @Override
    public List<Supplies> execute() {
        return this.suppliesRepository.findAll();
    }
}
