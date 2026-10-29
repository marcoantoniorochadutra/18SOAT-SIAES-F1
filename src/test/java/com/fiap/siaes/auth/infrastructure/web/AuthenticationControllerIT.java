package com.fiap.siaes.auth.infrastructure.web;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticateUserCommand;
import com.fiap.siaes.auth.application.usecase.RefreshAuthenticationUseCase.RefreshAuthenticationCommand;
import com.fiap.siaes.customer.infrastructure.web.CustomerController;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.utils.IntegrationTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Test - Authentication")
class AuthenticationControllerIT extends IntegrationTestUtils {

    @Test
    @DisplayName("[200] Deve autenticar e acessar endpoint protegido com o token")
    void shouldLoginAndAccessProtectedEndpoint() throws Exception {
        var token = super.loginAs(UserRole.MANAGER);

        super.executeGet(CustomerController.ENDPOINT, token)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[401] Não deve autenticar com senha incorreta")
    void shouldNotLoginWithWrongPassword() throws Exception {
        var email = super.persistUser(UserRole.MANAGER, null).getEmail();

        super.executePost(AuthenticationController.ENDPOINT + "/login", null,
                          AuthenticateUserCommand.of(email, "Errada@123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("[200] Deve renovar a autenticação com o refresh token")
    void shouldRefreshAuthentication() throws Exception {
        var refreshToken = this.loginReturningRefreshToken();

        super.executePost(AuthenticationController.ENDPOINT + "/refresh", null, RefreshAuthenticationCommand.of(refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.token", not(emptyOrNullString())))
                .andExpect(jsonValue("$.refreshToken", not(refreshToken)));
    }

    @Test
    @DisplayName("[401] Não deve reutilizar refresh token já rotacionado")
    void shouldNotReuseRotatedRefreshToken() throws Exception {
        var refreshToken = this.loginReturningRefreshToken();

        super.executePost(AuthenticationController.ENDPOINT + "/refresh", null, RefreshAuthenticationCommand.of(refreshToken))
                .andExpect(status().isOk());

        super.executePost(AuthenticationController.ENDPOINT + "/refresh", null, RefreshAuthenticationCommand.of(refreshToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("[200] Deve fazer logout e bloquear o token")
    void shouldLogoutAndBlockToken() throws Exception {
        var token = super.loginAs(UserRole.MANAGER);

        super.executePost(AuthenticationController.ENDPOINT + "/logout", token, null)
                .andExpect(status().isOk());

        super.executeGet(CustomerController.ENDPOINT, token)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("[401] Não deve fazer logout sem token")
    void shouldNotLogoutWithoutToken() throws Exception {
        super.executePost(AuthenticationController.ENDPOINT + "/logout", null, null)
                .andExpect(status().isUnauthorized());
    }

    private String loginReturningRefreshToken() {
        var email = super.persistUser(UserRole.MANAGER, null).getEmail();

        return super.login(email, PASSWORD, "$.refreshToken");
    }
}
