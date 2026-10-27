package com.fiap.siaes.auth.application.usecase;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticationWrapper;
import jakarta.validation.constraints.NotBlank;

public interface RefreshAuthenticationUseCase {

    AuthenticationWrapper execute(RefreshAuthenticationCommand command);

    record RefreshAuthenticationCommand(
            @NotBlank(message = "{AuthCommand.refreshToken.notBlank}")
            String refreshToken) {

        public static RefreshAuthenticationCommand of(String refreshToken) {
            return new RefreshAuthenticationCommand(refreshToken);
        }
    }
}
