package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.supplies.application.dto.SuppliesResponse;
import com.fiap.siaes.supplies.application.usecase.GetSuppliesByIdUseCase;
import com.fiap.siaes.supplies.domain.model.Supplies;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetSuppliesByIdAppService implements GetSuppliesByIdUseCase {

    private final SuppliesRepository suppliesRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public SuppliesResponse execute(SuppliesId id) {
        Supplies supplies = this.suppliesRepository.findByIdOrThrowNotFound(id);

        return SuppliesResponse.from(supplies);
    }
}
