package com.fiap.siaes.auth.application.usecase;

public interface AuthenticateUserUseCase {

    AuthenticationWrapper execute(AuthenticateUserCommand command);

    record AuthenticateUserCommand(String email, String password) {

        public static AuthenticateUserCommand of(String email, String password) {
            return new AuthenticateUserCommand(email, password);
        }
    }

    record AuthenticationWrapper(String token, String refreshToken) {
        public static AuthenticationWrapper of(String token, String refreshToken) {
            return new AuthenticationWrapper(token, refreshToken);
        }
    }
}
