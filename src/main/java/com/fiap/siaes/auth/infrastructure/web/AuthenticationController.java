package com.fiap.siaes.auth.infrastructure.web;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase;
import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticateUserCommand;
import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticationWrapper;
import com.fiap.siaes.auth.application.usecase.LogoutUserUseCase;
import com.fiap.siaes.auth.application.usecase.LogoutUserUseCase.LogoutUserCommand;
import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.auth.infrastructure.web.openapi.AuthenticationControllerOpenApi;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AuthenticationController.ENDPOINT)
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationControllerOpenApi {

    public static final String ENDPOINT = "/api/v1/auth";

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final LogoutUserUseCase logoutUserUseCase;

    @Override
    @PostMapping("/login")
    public AuthenticationWrapper login(@RequestBody AuthenticateUserCommand command) {
        return this.authenticateUserUseCase.execute(command);
    }

    @Override
    @PostMapping("/logout")
    @Authentication(minimumRole = UserRole.CUSTOMER)
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        this.logoutUserUseCase.execute(LogoutUserCommand.of(authorizationHeader));
    }

}
