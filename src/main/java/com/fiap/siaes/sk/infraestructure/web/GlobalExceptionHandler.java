package com.fiap.siaes.sk.infraestructure.web;

import com.fiap.siaes.sk.domain.exception.DomainException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;

import static com.fiap.siaes.sk.util.FunctionalUtils.nullSafeStream;
import static java.util.Comparator.comparing;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource i18nExceptionMessageSource;

    public GlobalExceptionHandler(@Qualifier("i18nExceptionMessageSource") MessageSource i18nExceptionMessageSource) {
        this.i18nExceptionMessageSource = i18nExceptionMessageSource;
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException exception, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(exception.getStatus());
        problemDetail.setTitle(this.translate(exception.getMessageKey(), exception.getParameters(), locale));
        problemDetail.setDetail(this.translate(exception.getDetailKey(), exception.getParameters(), locale));
        return problemDetail;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException exception, Locale locale) {
        String context = MethodArgumentNotValidException.class.getSimpleName();

        List<FieldErrorResponse> fields = nullSafeStream(exception.getBindingResult().getFieldErrors())
                .map(this::toFieldErrorResponse)
                .sorted(comparing(FieldErrorResponse::getField).thenComparing(FieldErrorResponse::getCode))
                .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle(this.translate(context + ".message", null, locale));
        problemDetail.setDetail(this.translate(context + ".detail", null, locale));
        problemDetail.setProperty("fields", fields);
        return problemDetail;
    }

    /**
     * {@code code} is the violated constraint (e.g. NotBlank, Size); {@code message} is already translated by the validator.
     */
    private FieldErrorResponse toFieldErrorResponse(FieldError fieldError) {
        return FieldErrorResponse.builder()
                .field(fieldError.getField())
                .code(fieldError.getCode())
                .message(fieldError.getDefaultMessage())
                .build();
    }

    private String translate(String key, Object[] parameters, Locale locale) {
        return this.i18nExceptionMessageSource.getMessage(key, parameters, locale);
    }
}
