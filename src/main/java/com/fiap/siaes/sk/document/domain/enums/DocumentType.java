package com.fiap.siaes.sk.document.domain.enums;

import com.fiap.siaes.sk.document.domain.exception.InvalidDocumentLengthException;

public enum DocumentType {
    CPF, CNPJ;

    public static DocumentType fromDigits(String digits) {
        switch (digits.length()) {
            case 11: return CPF;
            case 14: return CNPJ;
            default: throw new InvalidDocumentLengthException(digits);
        }
    }
}
