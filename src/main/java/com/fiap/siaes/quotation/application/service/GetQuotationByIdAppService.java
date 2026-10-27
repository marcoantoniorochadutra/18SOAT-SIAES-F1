package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.quotation.application.dto.QuotationResponse;
import com.fiap.siaes.quotation.application.usecase.GetQuotationByIdUseCase;
import com.fiap.siaes.quotation.domain.exception.QuotationNotFoundException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetQuotationByIdAppService implements GetQuotationByIdUseCase {

    private final QuotationRepository quotationRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public QuotationResponse execute(GetQuotationByIdCommand command) {
        Quotation quotation = this.quotationRepository.findByIdOrThrowNotFound(command.id());

        AuthenticatedUser authenticatedUser = command.authenticatedUser();
        if (authenticatedUser.isCustomer() && (authenticatedUser.isDifferentCustomer(quotation.getCustomerId()) || quotation.isDraft())) {
            throw new QuotationNotFoundException(command.id().toString());
        }

        Customer customer = this.customerRepository.findByIdOrThrowNotFound(quotation.getCustomerId());
        Vehicle vehicle = this.vehicleRepository.findByIdOrThrowNotFound(quotation.getVehicleId());
        return QuotationResponse.from(quotation, customer, vehicle);
    }
}
