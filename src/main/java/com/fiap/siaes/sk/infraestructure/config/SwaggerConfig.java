package com.fiap.siaes.sk.infraestructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

import static java.util.Objects.nonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
public class SwaggerConfig {

    private static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";
    private static final String ACCEPT_LANGUAGE_DESCRIPTION_KEY = "common.header.accept-language.description";
    private static final String NO_CONTENT_STATUS_CODE = "204";
    public static final String PT_BR = "pt-BR";
    public static final String EN_US = "en-US";

    @Bean
    public OpenAPI buildOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SIAES API")
                        .description("API do Sistema Integrado de Atendimento e Emissão de Serviços")
                        .version("v1"));
    }

    @Bean
    public GroupedOpenApi openApiTranslatedPTBR(MessageSource i18nSwaggerMessageSource) {
        Locale locale = Locale.forLanguageTag(PT_BR);
        return GroupedOpenApi.builder()
                .group(PT_BR)
                .pathsToMatch("/**")
                .addOperationCustomizer(this.swaggerTranslationResolver(i18nSwaggerMessageSource, locale))
                .addOpenApiCustomizer(this.swaggerTagTranslationResolver(i18nSwaggerMessageSource, locale))
                .build();
    }

    @Bean
    public GroupedOpenApi openApiTranslatedEN(MessageSource i18nSwaggerMessageSource) {
        return GroupedOpenApi.builder()
                .group(EN_US)
                .pathsToMatch("/**")
                .addOperationCustomizer(this.swaggerTranslationResolver(i18nSwaggerMessageSource, Locale.ENGLISH))
                .addOpenApiCustomizer(this.swaggerTagTranslationResolver(i18nSwaggerMessageSource, Locale.ENGLISH))
                .build();
    }

    private OperationCustomizer swaggerTranslationResolver(MessageSource i18nSwaggerMessageSource, Locale locale) {
        return (operation, handlerMethod) -> {
            this.setTranslatedSummary(i18nSwaggerMessageSource, locale, operation);
            this.setTranslatedOperation(i18nSwaggerMessageSource, locale, operation);
            this.setTranslatedOperationParameters(i18nSwaggerMessageSource, locale, operation);
            this.setTranslatedOperationResponses(i18nSwaggerMessageSource, locale, operation);
            return operation;
        };
    }

    private OpenApiCustomizer swaggerTagTranslationResolver(MessageSource i18nSwaggerMessageSource, Locale locale) {
        return openApi -> {
            if (nonNull(openApi.getTags())) {
                openApi.getTags()
                        .forEach(tag -> this.setTranslatedTagDescription(i18nSwaggerMessageSource, locale, tag));
            }
        };
    }

    private void setTranslatedTagDescription(MessageSource i18nSwaggerMessageSource, Locale locale, Tag tag) {
        if (nonNull(tag.getDescription())) {
            tag.setDescription(this.getTranslatedMessage(i18nSwaggerMessageSource, locale, tag.getDescription()));
        }
    }

    private void setTranslatedOperationResponses(MessageSource i18nSwaggerMessageSource, Locale locale, Operation operation) {
        if (nonNull(operation.getResponses())) {
            operation.getResponses().forEach((statusCode, response) -> {
                if (nonNull(response.getDescription())) {
                    response.setDescription(this.getTranslatedMessage(i18nSwaggerMessageSource, locale, response.getDescription()));

                    if (!NO_CONTENT_STATUS_CODE.equals(statusCode)) {
                        response.setContent(new Content().addMediaType(APPLICATION_JSON_VALUE, new MediaType()));
                    }
                }
            });
        }
    }

    private void setTranslatedOperationParameters(MessageSource i18nSwaggerMessageSource, Locale locale, Operation operation) {
        if (nonNull(operation.getParameters())) {
            operation.getParameters().stream()
                    .filter(parameter -> nonNull(parameter.getDescription()))
                    .forEach(parameter -> parameter.setDescription(
                            this.getTranslatedMessage(i18nSwaggerMessageSource, locale, parameter.getDescription())));
        }
    }

    private void setTranslatedOperation(MessageSource i18nSwaggerMessageSource, Locale locale, Operation operation) {
        if (nonNull(operation.getDescription())) {
            operation.setDescription(this.getTranslatedMessage(i18nSwaggerMessageSource, locale, operation.getDescription()));

            var parameter = new Parameter()
                    .in("HEADER")
                    .name(ACCEPT_LANGUAGE_HEADER)
                    .description(ACCEPT_LANGUAGE_DESCRIPTION_KEY)
                    .required(false)
                    .addExample(PT_BR, new Example().summary(PT_BR))
                    .addExample(EN_US, new Example().summary(EN_US));

            operation.addParametersItem(parameter);
        }
    }

    private void setTranslatedSummary(MessageSource i18nSwaggerMessageSource, Locale locale, Operation operation) {
        if (nonNull(operation.getSummary())) {
            operation.setSummary(this.getTranslatedMessage(i18nSwaggerMessageSource, locale, operation.getSummary()));
        }
    }

    private String getTranslatedMessage(MessageSource i18nSwaggerMessageSource, Locale locale, String operation) {
        return i18nSwaggerMessageSource.getMessage(operation, null, operation, locale);
    }

}
