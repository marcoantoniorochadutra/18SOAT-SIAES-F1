package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.application.dto.CustomerReferenceResponse;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalResponse;
import com.fiap.siaes.quotation.application.dto.QuotationResponse;
import com.fiap.siaes.quotation.application.service.helper.QuotationApprovalTokenHelper;
import com.fiap.siaes.quotation.application.usecase.GetQuotationByApprovalTokenUseCase;
import com.fiap.siaes.quotation.domain.exception.QuotationApprovalTokenInvalidException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.vehicle.application.dto.VehicleReferenceResponse;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetQuotationByApprovalTokenAppService implements GetQuotationByApprovalTokenUseCase {

    private final QuotationApprovalTokenHelper quotationApprovalTokenHelper;
    private final QuotationRepository quotationRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public QuotationApprovalResponse execute(GetQuotationByApprovalTokenCommand command) {
        QuotationApprovalToken token = this.quotationApprovalTokenHelper.findActive(command.token());
        Quotation quotation = this.quotationRepository.findByIdOrThrowNotFound(token.getQuotationId());

        if (!quotation.isAwaitingApproval())
            throw new QuotationApprovalTokenInvalidException();

        Customer customer = this.customerRepository.findByIdOrThrowNotFound(quotation.getCustomerId());
        Vehicle vehicle = this.vehicleRepository.findByIdOrThrowNotFound(quotation.getVehicleId());

        return QuotationApprovalResponse.of(QuotationResponse.from(quotation, CustomerReferenceResponse.from(customer), VehicleReferenceResponse.from(vehicle)), token.getExpiresAt());
    }
}
