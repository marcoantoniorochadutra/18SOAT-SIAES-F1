package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.quotation.application.service.helper.QuotationApprovalTokenHelper;
import com.fiap.siaes.quotation.application.service.helper.QuotationStockHelper;
import com.fiap.siaes.quotation.application.usecase.DecideQuotationUseCase;
import com.fiap.siaes.quotation.domain.exception.QuotationNotFoundException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class DecideQuotationAppService implements DecideQuotationUseCase {

    private final QuotationRepository quotationRepository;
    private final QuotationApprovalTokenHelper quotationApprovalTokenHelper;
    private final QuotationStockHelper quotationStockHelper;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(DecideQuotationCommand command) {
        Quotation quotation = this.quotationRepository.findByIdOrThrowNotFound(command.quotationId());

        if (nonNull(command.ownerId()) && !quotation.isOwnedBy(command.ownerId()))
            throw new QuotationNotFoundException(command.quotationId().toString());

        if (command.decision().isApprove()) {
            quotation.approve(command.channel(), command.decidedBy());
            this.quotationStockHelper.consumeAll(quotation, command.decidedBy(), command.decidedByName());
        } else {
            quotation.reject(command.channel(), command.decidedBy(), command.reason());
            this.quotationStockHelper.releaseAll(quotation, command.decidedBy(), command.decidedByName());
        }

        this.quotationRepository.save(quotation);

        this.quotationApprovalTokenHelper.revokeCurrent(quotation.getId());

        this.domainEventPublisher.publish(quotation.pullDomainEvents());
    }
}
