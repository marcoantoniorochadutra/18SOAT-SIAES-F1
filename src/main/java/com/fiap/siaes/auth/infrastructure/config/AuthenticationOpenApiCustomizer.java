package com.fiap.siaes.auth.infrastructure.config;

import com.fiap.siaes.auth.infrastructure.security.authorization.PublicEndpoint;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import static com.fiap.siaes.sk.infrastructure.config.SwaggerConfig.BEARER_AUTH;

@Component
public class AuthenticationOpenApiCustomizer implements GlobalOperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        if (!this.isPublic(handlerMethod)) {
            operation.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
        }

        return operation;
    }

    private boolean isPublic(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(PublicEndpoint.class)
                || handlerMethod.getBeanType().isAnnotationPresent(PublicEndpoint.class);
    }
}
