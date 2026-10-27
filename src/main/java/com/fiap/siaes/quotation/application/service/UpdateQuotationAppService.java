package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.quotation.application.service.assembler.QuotationAssembler;
import com.fiap.siaes.quotation.application.service.helper.QuotationStockHelper;
import com.fiap.siaes.quotation.application.usecase.UpdateQuotationUseCase;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UpdateQuotationAppService implements UpdateQuotationUseCase {

    private final QuotationRepository quotationRepository;
    private final QuotationAssembler quotationAssembler;
    private final QuotationStockHelper quotationStockHelper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public QuotationId execute(UpdateQuotationCommand command) {
        Quotation quotation = this.quotationRepository.findByIdOrThrowNotFound(command.id());

        Map<SuppliesId, Integer> previousQuantities = quotation.getSuppliesQuantities();

        quotation.update(command.description(), command.odometer(),
                         this.quotationAssembler.resolveSupplies(command.supplies()),
                         this.quotationAssembler.resolveMaintenances(command.maintenances()));

        this.quotationRepository.save(quotation);

        this.quotationStockHelper.release(previousQuantities, quotation, command.authenticatedUser().id());
        this.quotationStockHelper.reserve(previousQuantities, quotation, command.authenticatedUser().id());

        return quotation.getId();
    }
}
