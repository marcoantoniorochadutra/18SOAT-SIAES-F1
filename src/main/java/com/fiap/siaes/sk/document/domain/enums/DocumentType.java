package com.fiap.siaes.sk.document.domain.enums;

import com.fiap.siaes.sk.document.domain.exception.InvalidDocumentLengthException;

import static org.apache.commons.lang3.StringUtils.isBlank;

public enum DocumentType {
    CPF, CNPJ;

    public static DocumentType fromDigits(String digits) {
        if (isBlank(digits))
            return null;

        switch (digits.length()) {
            case 11: return CPF;
            case 14: return CNPJ;
            default: throw new InvalidDocumentLengthException(digits);
        }
    }
}
