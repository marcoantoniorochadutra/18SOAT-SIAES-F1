package com.fiap.siaes.quotation.application.service.helper;

import com.fiap.siaes.quotation.application.port.QuotationApprovalLinkProvider;
import com.fiap.siaes.quotation.domain.exception.QuotationApprovalTokenInvalidException;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.domain.repository.QuotationApprovalTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static com.fiap.siaes.quotation.QuotationTestFactory.createApprovalToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Quotation Approval Token Helper")
class QuotationApprovalTokenHelperTest {

    private static final String RAW_TOKEN = "raw-token";

    @Mock
    private QuotationApprovalTokenRepository quotationApprovalTokenRepository;

    @Mock
    private QuotationApprovalLinkProvider quotationApprovalLinkProvider;

    @InjectMocks
    private QuotationApprovalTokenHelper quotationApprovalTokenHelper;

    @Test
    @DisplayName("Deve emitir um novo token revogando o token ativo anterior")
    void shouldIssueTokenRevokingPrevious() {
        var quotationId = QuotationId.generate();
        QuotationApprovalToken previous = createApprovalToken(quotationId);

        when(this.quotationApprovalTokenRepository.findLatestByQuotationId(quotationId)).thenReturn(Optional.of(previous));
        when(this.quotationApprovalLinkProvider.timeToLive()).thenReturn(Duration.ofHours(72));

        QuotationApprovalToken token = this.quotationApprovalTokenHelper.issue(quotationId);

        assertFalse(previous.isActive());
        assertTrue(token.isActive());
        assertNotEquals(previous.getId(), token.getId());
        verify(this.quotationApprovalTokenRepository).save(previous);
        verify(this.quotationApprovalTokenRepository).save(token);
    }

    @Test
    @DisplayName("Deve reaproveitar o token ativo ao buscar o link atual")
    void shouldReuseActiveToken() {
        var quotationId = QuotationId.generate();
        QuotationApprovalToken current = createApprovalToken(quotationId);

        when(this.quotationApprovalTokenRepository.findLatestByQuotationId(quotationId)).thenReturn(Optional.of(current));

        assertSame(current, this.quotationApprovalTokenHelper.currentOrIssue(quotationId));
        verify(this.quotationApprovalTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve emitir um novo token quando o atual estiver expirado")
    void shouldIssueTokenWhenCurrentExpired() {
        var quotationId = QuotationId.generate();
        QuotationApprovalToken expired = createApprovalToken(quotationId, Instant.now().minusSeconds(1));

        when(this.quotationApprovalTokenRepository.findLatestByQuotationId(quotationId)).thenReturn(Optional.of(expired));
        when(this.quotationApprovalLinkProvider.timeToLive()).thenReturn(Duration.ofHours(72));

        QuotationApprovalToken token = this.quotationApprovalTokenHelper.currentOrIssue(quotationId);

        assertNotEquals(expired.getId(), token.getId());
        verify(this.quotationApprovalTokenRepository).save(token);
    }

    @Test
    @DisplayName("Deve encontrar o token ativo a partir do token do link")
    void shouldFindActiveToken() {
        QuotationApprovalToken token = createApprovalToken(QuotationId.generate());

        when(this.quotationApprovalLinkProvider.resolve(RAW_TOKEN)).thenReturn(Optional.of(token.getId()));
        when(this.quotationApprovalTokenRepository.findByIdOrThrowInvalid(token.getId())).thenReturn(token);

        assertEquals(token, this.quotationApprovalTokenHelper.findActive(RAW_TOKEN));
    }

    @Test
    @DisplayName("Não deve aceitar token com assinatura inválida, inexistente ou expirado")
    void shouldNotFindInvalidToken() {
        QuotationApprovalToken expired = createApprovalToken(QuotationId.generate(), Instant.now().minusSeconds(1));
        QuotationApprovalToken missing = createApprovalToken(QuotationId.generate());

        when(this.quotationApprovalLinkProvider.resolve("invalid")).thenReturn(Optional.empty());
        when(this.quotationApprovalLinkProvider.resolve("missing")).thenReturn(Optional.of(missing.getId()));
        when(this.quotationApprovalLinkProvider.resolve("expired")).thenReturn(Optional.of(expired.getId()));
        when(this.quotationApprovalTokenRepository.findByIdOrThrowInvalid(missing.getId()))
                .thenThrow(new QuotationApprovalTokenInvalidException());
        when(this.quotationApprovalTokenRepository.findByIdOrThrowInvalid(expired.getId())).thenReturn(expired);

        assertThrows(QuotationApprovalTokenInvalidException.class, () -> this.quotationApprovalTokenHelper.findActive("invalid"));
        assertThrows(QuotationApprovalTokenInvalidException.class, () -> this.quotationApprovalTokenHelper.findActive("missing"));
        assertThrows(QuotationApprovalTokenInvalidException.class, () -> this.quotationApprovalTokenHelper.findActive("expired"));
    }

    @Test
    @DisplayName("Deve gerar o link de aprovação do token")
    void shouldGenerateLinkForToken() {
        QuotationApprovalToken token = createApprovalToken(QuotationId.generate());

        when(this.quotationApprovalLinkProvider.linkFor(token)).thenReturn("https://siaes.com/quotationApprovals/token");

        assertEquals("https://siaes.com/quotationApprovals/token", this.quotationApprovalTokenHelper.linkFor(token));
    }
}
