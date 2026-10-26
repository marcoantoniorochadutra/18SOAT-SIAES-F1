package com.fiap.siaes.quotation.infrastructure.security;

import com.fiap.siaes.quotation.application.port.QuotationApprovalLinkProvider;
import com.fiap.siaes.quotation.domain.model.QuotationApprovalToken;
import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId;
import com.fiap.siaes.sk.domain.exception.InternalException;
import com.fiap.siaes.sk.util.ControllerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public class SignedQuotationApprovalLinkProvider implements QuotationApprovalLinkProvider {

    private static final String ALGORITHM = "HmacSHA256";
    private static final String SEPARATOR = ".";

    private final QuotationApprovalProperties quotationApprovalProperties;

    @Override
    public Duration timeToLive() {
        return this.quotationApprovalProperties.timeToLive();
    }

    @Override
    public String linkFor(QuotationApprovalToken token) {
        String rawToken = token.getId() + SEPARATOR + this.sign(token.getId().toString());

        return ControllerUtils.buildUrl(this.quotationApprovalProperties.linkBaseUrl(), rawToken);
    }

    @Override
    public Optional<QuotationApprovalTokenId> resolve(String rawToken) {
        if (isBlank(rawToken) || !rawToken.contains(SEPARATOR))
            return Optional.empty();

        String id = rawToken.substring(0, rawToken.indexOf(SEPARATOR));
        String signature = rawToken.substring(rawToken.indexOf(SEPARATOR) + 1);

        byte[] expected = this.sign(id).getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expected, signature.getBytes(StandardCharsets.UTF_8)))
            return Optional.empty();

        try {
            return Optional.of(QuotationApprovalTokenId.from(UUID.fromString(id)));
        } catch (IllegalArgumentException _) {
            return Optional.empty();
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(this.quotationApprovalProperties.secret().getBytes(StandardCharsets.UTF_8), ALGORITHM));

            byte[] signature = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (GeneralSecurityException exception) {
            throw new InternalException("Unable to sign the quotation approval token", exception);
        }
    }
}
