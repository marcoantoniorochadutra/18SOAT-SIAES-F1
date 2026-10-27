package com.fiap.siaes.auth.application.usecase;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;

public interface LogoutUserUseCase {

    void execute(LogoutUserCommand command);

    record LogoutUserCommand(String token, AuthenticatedUser authenticatedUser) {
        public static LogoutUserCommand of(String token, AuthenticatedUser authenticatedUser) {
            return new LogoutUserCommand(token, authenticatedUser);
        }
    }
}
