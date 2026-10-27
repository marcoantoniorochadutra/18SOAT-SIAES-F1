package com.fiap.siaes.quotation.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.application.dto.QuotationApprovalLinkResponse;
import com.fiap.siaes.quotation.application.service.helper.QuotationApprovalTokenHelper;
import com.fiap.siaes.quotation.application.usecase.GetQuotationApprovalLinkUseCase.GetQuotationApprovalLinkCommand;
import com.fiap.siaes.quotation.domain.exception.QuotationNotAwaitingApprovalException;
import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.quotation.QuotationTestFactory.createApprovalToken;
import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Get Quotation Approval Link")
class GetQuotationApprovalLinkAppServiceTest {

    @Mock
    private QuotationRepository quotationRepository;

    @Mock
    private QuotationApprovalTokenHelper quotationApprovalTokenHelper;

    @InjectMocks
    private GetQuotationApprovalLinkAppService getQuotationApprovalLinkAppService;

    @Test
    @DisplayName("Deve retornar o link do token atual do orçamento")
    void shouldReturnCurrentLink() {
        Quotation quotation = createQuotation();
        QuotationApprovalToken token = createApprovalToken(quotation.getId());

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);
        when(this.quotationApprovalTokenHelper.currentOrIssue(quotation.getId())).thenReturn(token);
        when(this.quotationApprovalTokenHelper.linkFor(token)).thenReturn("https://siaes.com/quotationApprovals/token");

        QuotationApprovalLinkResponse response = this.getQuotationApprovalLinkAppService.execute(
                GetQuotationApprovalLinkCommand.of(quotation.getId()));

        assertEquals("https://siaes.com/quotationApprovals/token", response.link());
        assertEquals(token.getExpiresAt(), response.expiresAt());
    }

    @Test
    @DisplayName("Não deve gerar link para orçamento que não aguarda aprovação")
    void shouldNotReturnLinkWhenQuotationAlreadyDecided() {
        Quotation quotation = createQuotation(CustomerId.generate(), QuotationStatus.REJECTED);

        when(this.quotationRepository.findByIdOrThrowNotFound(quotation.getId())).thenReturn(quotation);

        assertThrows(QuotationNotAwaitingApprovalException.class,
                     () -> this.getQuotationApprovalLinkAppService.execute(GetQuotationApprovalLinkCommand.of(quotation.getId())));

        verifyNoInteractions(this.quotationApprovalTokenHelper);
    }
}
