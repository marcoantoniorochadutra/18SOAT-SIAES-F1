package com.fiap.siaes.quotation.infrastructure.security;

import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Security - Quotation Approval Link")
class SignedQuotationApprovalLinkProviderTest {

    private static final String BASE_URL = "https://siaes.com/quotationApprovals";

    private static final String SECRET = "secret-0123456789";

    private final SignedQuotationApprovalLinkProvider provider = this.provider(SECRET);

    @Test
    @DisplayName("Deve retornar a validade configurada para o link")
    void shouldReturnConfiguredTimeToLive() {
        assertEquals(Duration.ofHours(72), this.provider.timeToLive());
    }

    @Test
    @DisplayName("Não deve resolver token com assinatura válida e id que não seja UUID")
    void shouldNotResolveSignedTokenWithInvalidId() throws Exception {
        String id = "not-a-uuid";

        assertTrue(this.provider.resolve(id + "." + this.sign(id)).isEmpty());
    }

    @Test
    @DisplayName("Deve gerar sempre o mesmo link para o mesmo token")
    void shouldGenerateSameLinkForSameToken() {
        QuotationApprovalToken token = this.token();

        String link = this.provider.linkFor(token);

        assertTrue(link.startsWith(BASE_URL + "/" + token.getId() + "."));
        assertEquals(link, this.provider.linkFor(token));
    }

    @Test
    @DisplayName("Deve resolver o id do token a partir do link gerado")
    void shouldResolveTokenId() {
        QuotationApprovalToken token = this.token();

        String rawToken = this.rawTokenOf(this.provider.linkFor(token));

        assertEquals(token.getId(), this.provider.resolve(rawToken).orElseThrow());
    }

    @Test
    @DisplayName("Não deve resolver token com assinatura adulterada ou de outro segredo")
    void shouldNotResolveTamperedToken() {
        QuotationApprovalToken token = this.token();
        String rawToken = this.rawTokenOf(this.provider.linkFor(token));
        String otherSecretToken = this.rawTokenOf(this.provider("other-secret-0123456789").linkFor(token));

        assertTrue(this.provider.resolve(rawToken + "x").isEmpty());
        assertTrue(this.provider.resolve(otherSecretToken).isEmpty());
        assertTrue(this.provider.resolve("not-a-token").isEmpty());
        assertTrue(this.provider.resolve(null).isEmpty());
    }

    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

        return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }

    private String rawTokenOf(String link) {
        return link.substring(link.lastIndexOf('/') + 1);
    }

    private QuotationApprovalToken token() {
        return QuotationApprovalToken.builder()
                .quotationId(QuotationId.generate())
                .timeToLive(Duration.ofHours(72))
                .build();
    }

    private SignedQuotationApprovalLinkProvider provider(String secret) {
        return new SignedQuotationApprovalLinkProvider(new QuotationApprovalProperties(secret, Duration.ofHours(72), BASE_URL));
    }
}
