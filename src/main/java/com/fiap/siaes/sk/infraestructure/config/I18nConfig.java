package com.fiap.siaes.sk.infraestructure.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Locale;

@Configuration
public class I18nConfig {

    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("pt-BR");

    private static final String SWAGGER_MESSAGE_FILEPATH = "messages/swagger/messages";
    private static final String EXCEPTION_MESSAGE_FILEPATH = "messages/exception/messages";
    private static final String VALIDATION_MESSAGE_FILEPATH = "messages/validation/messages";

    @Bean
    public MessageSource i18nSwaggerMessageSource() {
        return this.createMessageMessageSource(SWAGGER_MESSAGE_FILEPATH);
    }

    @Bean
    public MessageSource i18nExceptionMessageSource() {
        return this.createMessageMessageSource(EXCEPTION_MESSAGE_FILEPATH);
    }

    @Bean
    public MessageSource i18nValidationMessageSource() {
        ResourceBundleMessageSource messageSource = this.createMessageMessageSource(VALIDATION_MESSAGE_FILEPATH);
        messageSource.setUseCodeAsDefaultMessage(false);
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator(MessageSource i18nValidationMessageSource) {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(i18nValidationMessageSource);
        return validator;
    }

    private ResourceBundleMessageSource createMessageMessageSource(String baseName) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(baseName);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(DEFAULT_LOCALE);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
}
