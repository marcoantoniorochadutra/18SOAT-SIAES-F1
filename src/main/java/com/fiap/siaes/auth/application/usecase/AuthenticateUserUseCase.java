package com.fiap.siaes.auth.application.usecase;

public interface AuthenticateUserUseCase {

    AuthToken execute(AuthenticateCommand command);

    record AuthenticateCommand(String email, String password) {}

    record AuthToken(String token) {}
}
