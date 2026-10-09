package com.fiap.siaes.auth.application.usecase;

public interface LogoutUserUseCase {

    void execute(LogoutUserCommand command);

    record LogoutUserCommand(String token) {
        public static LogoutUserCommand of(String token) {
            return new LogoutUserCommand(token);
        }
    }
}
