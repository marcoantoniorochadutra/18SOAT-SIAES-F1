package com.fiap.siaes.customer.application.service;

import com.fiap.siaes.customer.application.dto.CustomerResponse;
import com.fiap.siaes.customer.application.usecase.ListCustomersUseCase;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.sk.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListCustomersAppService implements ListCustomersUseCase {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public PageResponse<CustomerResponse> execute(ListCustomersCommand command) {
        return this.customerRepository.findAllByFilter(command.filter(), command.pageQuery());
    }
}
