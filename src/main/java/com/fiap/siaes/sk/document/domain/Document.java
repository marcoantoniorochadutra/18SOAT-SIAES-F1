package com.fiap.siaes.sk.document.domain;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import com.fiap.siaes.sk.document.domain.enums.DocumentType;
import com.fiap.siaes.sk.document.domain.exception.DocumentValueIsNullException;
import com.fiap.siaes.sk.document.domain.exception.InvalidDocumentException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Document {

    @EqualsAndHashCode.Include
    private final String value;
    private final DocumentType documentType;

    public Document(String value) {
        if (isBlank(value)) {
            throw new DocumentValueIsNullException();
        }

        this.value = this.sanitizeDocument(value);
        this.documentType = DocumentType.fromDigits(this.value);
        this.validateDocument();
    }

    public static Document create(String value) {
        return new Document(value);
    }

    public static Document recreate(String value) {
        return new Document(value, DocumentType.fromDigits(value));
    }

    private void validateDocument() {
        try {
            if (this.documentType.equals(DocumentType.CPF)) {
                new CPFValidator().assertValid(this.value);
                return;
            }

            new CNPJValidator().assertValid(this.value);
        } catch (InvalidStateException _) {
            throw new InvalidDocumentException(this.value);
        }
    }

    private String sanitizeDocument(String value) {
        return value.replaceAll("\\D", "");
    }
}
