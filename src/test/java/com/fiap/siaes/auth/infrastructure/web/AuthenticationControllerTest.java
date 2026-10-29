package com.fiap.siaes.auth.infrastructure.web;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase;
import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticateUserCommand;
import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticationWrapper;
import com.fiap.siaes.auth.application.usecase.LogoutUserUseCase;
import com.fiap.siaes.auth.application.usecase.LogoutUserUseCase.LogoutUserCommand;
import com.fiap.siaes.auth.application.usecase.RefreshAuthenticationUseCase;
import com.fiap.siaes.auth.application.usecase.RefreshAuthenticationUseCase.RefreshAuthenticationCommand;
import com.fiap.siaes.utils.ControllerTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Controller Test - Authentication")
class AuthenticationControllerTest extends ControllerTestUtils {

    private static final String TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final AuthenticationWrapper AUTHENTICATION = AuthenticationWrapper.of(TOKEN, REFRESH_TOKEN);

    @MockitoBean
    private AuthenticateUserUseCase authenticateUserUseCase;

    @MockitoBean
    private RefreshAuthenticationUseCase refreshAuthenticationUseCase;

    @MockitoBean
    private LogoutUserUseCase logoutUserUseCase;

    @Test
    @DisplayName("[200] Deve autenticar usuário com sucesso")
    void shouldLogin() throws Exception {
        var command = AuthenticateUserCommand.of("maria@email.com", "password");

        when(this.authenticateUserUseCase.execute(command)).thenReturn(AUTHENTICATION);

        super.executePost(AuthenticationController.ENDPOINT + "/login", command)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(REFRESH_TOKEN));
    }

    @Test
    @DisplayName("[200] Deve renovar autenticação com sucesso")
    void shouldRefresh() throws Exception {
        var command = RefreshAuthenticationCommand.of(REFRESH_TOKEN);

        when(this.refreshAuthenticationUseCase.execute(command)).thenReturn(AUTHENTICATION);

        super.executePost(AuthenticationController.ENDPOINT + "/refresh", command)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TOKEN))
                .andExpect(jsonPath("$.refreshToken").value(REFRESH_TOKEN));
    }

    @Test
    @DisplayName("[400] Não deve renovar autenticação sem refresh token")
    void shouldNotRefreshWhenRefreshTokenIsBlank() throws Exception {
        super.executePost(AuthenticationController.ENDPOINT + "/refresh", RefreshAuthenticationCommand.of(" "))
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("AuthCommand.refreshToken.notBlank"));

        verifyNoInteractions(this.refreshAuthenticationUseCase);
    }

    @Test
    @DisplayName("[200] Deve fazer logout com o token do cabeçalho")
    void shouldLogout() throws Exception {
        super.authenticateAs(AUTHENTICATED_USER_ADMIN);

        this.mockMvc.perform(post(AuthenticationController.ENDPOINT + "/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .locale(LocaleContextHolder.getLocale()))
                .andExpect(status().isOk());

        verify(this.logoutUserUseCase).execute(LogoutUserCommand.of(TOKEN, AUTHENTICATED_USER_ADMIN));
    }
}
