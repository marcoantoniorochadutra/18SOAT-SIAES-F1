package com.fiap.siaes.supplies.application.service;

import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.supplies.application.dto.SuppliesResponse;
import com.fiap.siaes.supplies.application.port.SuppliesQueryPort;
import com.fiap.siaes.supplies.application.usecase.ListSuppliesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListSuppliesAppService implements ListSuppliesUseCase {

    private final SuppliesQueryPort suppliesQueryPort;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public PageResponse<SuppliesResponse> execute(ListSuppliesCommand command) {
        return this.suppliesQueryPort.findAllByFilter(command.filter(), command.pageQuery());
    }
}
