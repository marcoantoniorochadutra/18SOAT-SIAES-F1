package com.fiap.siaes.auth.infrastructure.config;

import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.auth.infrastructure.security.authorization.PublicEndpoint;
import io.swagger.v3.oas.models.Operation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import static com.fiap.siaes.sk.infrastructure.config.SwaggerConfig.BEARER_AUTH;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - OpenAPI - Authentication Customizer")
class AuthenticationOpenApiCustomizerTest {

    @InjectMocks
    private AuthenticationOpenApiCustomizer authenticationOpenApiCustomizer;

    @Test
    @DisplayName("Deve exigir o token Bearer nos endpoints autenticados")
    void shouldRequireBearerTokenOnAuthenticatedEndpoints() throws NoSuchMethodException {
        var operation = this.authenticationOpenApiCustomizer.customize(new Operation(), this.handler(new SecuredController(), "secured"));

        assertTrue(operation.getSecurity().getFirst().containsKey(BEARER_AUTH));
    }

    @Test
    @DisplayName("Não deve exigir token em método marcado como público")
    void shouldNotRequireTokenOnPublicMethod() throws NoSuchMethodException {
        var operation = this.authenticationOpenApiCustomizer.customize(new Operation(), this.handler(new SecuredController(), "login"));

        assertNull(operation.getSecurity());
    }

    @Test
    @DisplayName("Não deve exigir token em controller marcado como público")
    void shouldNotRequireTokenOnPublicController() throws NoSuchMethodException {
        var operation = this.authenticationOpenApiCustomizer.customize(new Operation(), this.handler(new PublicController(), "open"));

        assertNull(operation.getSecurity());
    }

    private HandlerMethod handler(Object bean, String method) throws NoSuchMethodException {
        return new HandlerMethod(bean, bean.getClass().getDeclaredMethod(method));
    }

    @Authentication
    static class SecuredController {

        void secured() {
        }

        @PublicEndpoint
        void login() {
        }
    }

    @PublicEndpoint
    static class PublicController {

        void open() {
        }
    }
}
