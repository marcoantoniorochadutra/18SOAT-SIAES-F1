package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.quotation.application.usecase.CompleteQuotationUseCase;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.sk.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompleteQuotationAppService implements CompleteQuotationUseCase {

    private final QuotationRepository quotationRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(CompleteQuotationCommand command) {
        Quotation quotation = this.quotationRepository.findByIdOrThrowNotFound(command.id());

        quotation.complete();

        this.quotationRepository.save(quotation);

        this.domainEventPublisher.publish(quotation.pullDomainEvents());
    }
}
