package com.fiap.siaes.auth.infrastructure.web;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase;
import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthToken;
import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticateCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Autenticação de usuários")
@RestController
@RequestMapping(AuthController.ENDPOINT)
@RequiredArgsConstructor
public class AuthController {

    public static final String ENDPOINT = "/api/v1/auth";

    private final AuthenticateUserUseCase authenticateUserUseCase;

    @Operation(summary = "Autentica um usuário e retorna um token")
    @PostMapping("/login")
    public AuthToken login(@RequestBody AuthenticateCommand command) {
        return this.authenticateUserUseCase.execute(command);
    }
}
