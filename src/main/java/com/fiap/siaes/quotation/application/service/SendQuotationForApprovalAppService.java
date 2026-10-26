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
import com.fiap.siaes.sk.domain.exception.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
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

        if (quotation.getLastStatus().isNotAwaitingApproval())
            return;

        Customer customer = this.customerRepository.findByIdOrThrowNotFound(quotation.getCustomerId());

        if (isBlank(customer.getEmail()) && isBlank(customer.getPhone())) {
            log.warn("Customer {} has no e-mail or phone; the approval link of quotation {} must be shared by the workshop", customer.getId(), quotation.getId());
            return;
        }

        QuotationApprovalToken token = this.quotationApprovalTokenHelper.issue(quotation.getId());
        String link = this.quotationApprovalTokenHelper.linkFor(token);

        if (isNotBlank(customer.getEmail())) {
            this.send(NotificationChannel.EMAIL, customer.getEmail(), customer, link, token);
        }

        if (isNotBlank(customer.getPhone())) {
            this.send(NotificationChannel.WHATSAPP, customer.getPhone(), customer, link, token);
        }
    }

    private void send(NotificationChannel channel, String recipient, Customer customer, String link,
                      QuotationApprovalToken token) {
        this.notifierFor(channel)
                .send(QuotationApprovalNotification.of(recipient, customer.getName(), link, token.getExpiresAt()));
    }

    private QuotationApprovalNotifier notifierFor(NotificationChannel channel) {
        return this.quotationApprovalNotifiers.stream()
                .filter(notifier -> channel.equals(notifier.channel()))
                .findFirst()
                .orElseThrow(() -> new InternalException("No quotation approval notifier for channel " + channel));
    }
}
