package com.fiap.siaes.sk.document.domain.enums;

import com.fiap.siaes.sk.document.domain.exception.InvalidDocumentLengthException;

import static java.util.Objects.isNull;

public enum DocumentType {
    CPF, CNPJ;

    public static DocumentType fromDigits(String digits) {
        int length = isNull(digits) ? 0 : digits.length();

        return switch (length) {
            case 11 -> CPF;
            case 14 -> CNPJ;
            default -> throw new InvalidDocumentLengthException(digits);
        };
    }
}
