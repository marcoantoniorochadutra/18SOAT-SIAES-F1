package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.quotation.application.dto.NotificationChannel;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalNotification;
import com.fiap.siaes.quotation.application.port.QuotationApprovalNotifier;
import com.fiap.siaes.quotation.application.service.helper.QuotationApprovalTokenHelper;
import com.fiap.siaes.quotation.application.usecase.SendQuotationForApprovalUseCase;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendQuotationForApprovalAppService implements SendQuotationForApprovalUseCase {

    private final QuotationRepository quotationRepository;
    private final CustomerRepository customerRepository;
    private final QuotationApprovalTokenHelper quotationApprovalTokenHelper;
    private final List<QuotationApprovalNotifier> quotationApprovalNotifiers;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execute(SendQuotationForApprovalCommand command) {
        Quotation quotation = this.quotationRepository.findByIdOrThrowNotFound(command.quotationId());

        if (!quotation.isAwaitingApproval())
            return;

        Customer customer = this.customerRepository.findByIdOrThrowNotFound(quotation.getCustomerId());

        List<QuotationApprovalNotifier> notifiers = this.quotationApprovalNotifiers.stream()
                .filter(notifier -> isNotBlank(this.recipientFor(notifier.channel(), customer)))
                .toList();

        if (notifiers.isEmpty()) {
            log.warn("No available channel reaches customer {}; the approval link of quotation {} must be shared by the workshop", customer.getId(), quotation.getId());
            return;
        }

        QuotationApprovalToken token = this.quotationApprovalTokenHelper.issue(quotation.getId());
        String link = this.quotationApprovalTokenHelper.linkFor(token);

        notifiers.forEach(notifier -> notifier.send(QuotationApprovalNotification.of(this.recipientFor(notifier.channel(), customer),
                                                                           customer.getName(), link, token.getExpiresAt())));
    }

    private String recipientFor(NotificationChannel channel, Customer customer) {
        return switch (channel) {
            case EMAIL -> customer.getEmail();
            case WHATSAPP -> customer.getPhone();
        };
    }
}
