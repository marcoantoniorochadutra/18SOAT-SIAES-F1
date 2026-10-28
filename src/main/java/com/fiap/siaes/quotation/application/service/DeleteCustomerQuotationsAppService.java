package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.quotation.application.service.helper.QuotationStockHelper;
import com.fiap.siaes.quotation.application.usecase.DeleteCustomerQuotationsUseCase;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteCustomerQuotationsAppService implements DeleteCustomerQuotationsUseCase {

    private final QuotationRepository quotationRepository;
    private final QuotationStockHelper quotationStockHelper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(DeleteCustomerQuotationsCommand command) {
        this.quotationRepository.findAllByCustomerId(command.customerId()).stream()
                .filter(Quotation::holdsStockReservation)
                .forEach(quotation -> this.quotationStockHelper.releaseAll(quotation, command.deletedBy(), null));

        this.quotationRepository.deleteAllByCustomerId(command.customerId());
    }
}
