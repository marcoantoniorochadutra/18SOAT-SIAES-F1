package com.fiap.siaes.sk.infraestructure.web;

import com.fiap.siaes.customer.domain.exception.CustomerEmailAlreadyExistsException;
import com.fiap.siaes.sk.document.domain.Document;
import com.fiap.siaes.sk.document.domain.exception.InvalidDocumentException;
import com.fiap.siaes.sk.infraestructure.config.I18nConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("GlobalExceptionHandler - Unit Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(new I18nConfig().i18nExceptionMessageSource());

    @Test
    @DisplayName("Deve traduzir exceção de domínio com parâmetros para português")
    void translatesDomainExceptionWithParametersInPortuguese() {
        ProblemDetail problem = this.handler.handleDomainException(
                new CustomerEmailAlreadyExistsException("joao@email.com"), Locale.forLanguageTag("pt-BR"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getTitle()).isEqualTo("E-mail já cadastrado");
        assertThat(problem.getDetail()).isEqualTo("Já existe um cliente cadastrado com o e-mail joao@email.com.");
    }

    @Test
    @DisplayName("Deve traduzir exceção de documento inválido para inglês")
    void translatesDocumentExceptionThrownByDomainInEnglish() {
        assertThatThrownBy(() -> Document.create("111.111.111-12"))
                .isInstanceOfSatisfying(InvalidDocumentException.class, exception -> {
                    ProblemDetail problem = this.handler.handleDomainException(exception, Locale.ENGLISH);

                    assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(problem.getTitle()).isEqualTo("Invalid document");
                    assertThat(problem.getDetail()).isEqualTo(
                            "The CPF/CNPJ 11111111112 is not valid. Check the verification digits and try again.");
                });
    }

    @Test
    void fallsBackToPortugueseForUnsupportedLanguageRegardlessOfServerLocale() {
        Locale serverLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        try {
            GlobalExceptionHandler handlerOnEnglishServer =
                    new GlobalExceptionHandler(new I18nConfig().i18nExceptionMessageSource());

            ProblemDetail problem = handlerOnEnglishServer.handleDomainException(
                    new CustomerEmailAlreadyExistsException("joao@email.com"), Locale.FRENCH);

            assertThat(problem.getTitle()).isEqualTo("E-mail já cadastrado");
        } finally {
            Locale.setDefault(serverLocale);
        }
    }

    @Test
    void resolvesPortugueseWithoutCountry() {
        ProblemDetail problem = this.handler.handleDomainException(
                new CustomerEmailAlreadyExistsException("joao@email.com"), Locale.forLanguageTag("pt"));

        assertThat(problem.getTitle()).isEqualTo("E-mail já cadastrado");
    }
}
