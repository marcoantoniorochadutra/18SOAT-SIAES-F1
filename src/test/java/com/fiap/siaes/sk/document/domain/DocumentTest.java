package com.fiap.siaes.sk.document.domain;

import com.fiap.siaes.sk.document.domain.enums.DocumentType;
import com.fiap.siaes.sk.document.domain.exception.DocumentValueIsNullException;
import com.fiap.siaes.sk.document.domain.exception.InvalidDocumentException;
import com.fiap.siaes.sk.document.domain.exception.InvalidDocumentLengthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - Document")
class DocumentTest {

    private static final Document DOCUMENT = Document.recreate("12345678909");

    @Test
    @DisplayName("Deve conferir os últimos dígitos do documento")
    void shouldMatchLastDigits() {
        assertTrue(DOCUMENT.endsWith("8909"));
        assertTrue(DOCUMENT.endsWith("89-09"));
    }

    @Test
    @DisplayName("Não deve conferir últimos dígitos diferentes ou vazios")
    void shouldNotMatchDifferentOrBlankDigits() {
        assertFalse(DOCUMENT.endsWith("1234"));
        assertFalse(DOCUMENT.endsWith(" "));
        assertFalse(DOCUMENT.endsWith(null));
    }

    @Test
    @DisplayName("Deve criar CPF e CNPJ válidos sem formatação")
    void shouldCreateValidCpfAndCnpj() {
        var cpf = Document.create("529.982.247-25");
        var cnpj = Document.create("11.222.333/0001-81");

        assertEquals("52998224725", cpf.getValue());
        assertEquals(DocumentType.CPF, cpf.getDocumentType());
        assertEquals("11222333000181", cnpj.getValue());
        assertEquals(DocumentType.CNPJ, cnpj.getDocumentType());
    }

    @Test
    @DisplayName("Não deve criar documento em branco")
    void shouldNotCreateBlankDocument() {
        assertThrows(DocumentValueIsNullException.class, () -> Document.create(" "));
        assertThrows(DocumentValueIsNullException.class, () -> Document.create(null));
    }

    @Test
    @DisplayName("Não deve criar documento com tamanho inválido")
    void shouldNotCreateDocumentWithInvalidLength() {
        assertThrows(InvalidDocumentLengthException.class, () -> Document.create("123456"));
    }

    @Test
    @DisplayName("Não deve criar CPF ou CNPJ com dígitos verificadores inválidos")
    void shouldNotCreateDocumentWithInvalidCheckDigits() {
        assertThrows(InvalidDocumentException.class, () -> Document.create("529.982.247-26"));
        assertThrows(InvalidDocumentException.class, () -> Document.create("11.222.333/0001-82"));
    }

    @Test
    @DisplayName("Não deve sanitizar valor em branco")
    void shouldNotSanitizeBlankValue() {
        assertNull(Document.sanitize(" "));
    }
}
