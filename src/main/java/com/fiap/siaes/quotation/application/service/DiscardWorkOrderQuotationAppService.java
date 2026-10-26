package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.quotation.application.service.helper.QuotationStockHelper;
import com.fiap.siaes.quotation.application.usecase.DiscardWorkOrderQuotationUseCase;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiscardWorkOrderQuotationAppService implements DiscardWorkOrderQuotationUseCase {

    private final QuotationRepository quotationRepository;
    private final QuotationStockHelper quotationStockHelper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(DiscardWorkOrderQuotationCommand command) {
        this.quotationRepository.findDraftByWorkOrderId(command.workOrderId()).ifPresent(quotation -> {
            quotation.discard();

            this.quotationRepository.save(quotation);

            this.quotationStockHelper.releaseAll(quotation, command.discardedBy(), null);
        });
    }
}
