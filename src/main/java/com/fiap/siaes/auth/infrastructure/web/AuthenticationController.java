package com.fiap.siaes.auth.infrastructure.web;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase;
import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticateUserCommand;
import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticationWrapper;
import com.fiap.siaes.auth.application.usecase.LogoutUserUseCase;
import com.fiap.siaes.auth.application.usecase.LogoutUserUseCase.LogoutUserCommand;
import com.fiap.siaes.auth.application.usecase.RefreshAuthenticationUseCase;
import com.fiap.siaes.auth.application.usecase.RefreshAuthenticationUseCase.RefreshAuthenticationCommand;
import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.auth.infrastructure.web.openapi.AuthenticationControllerOpenApi;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final RefreshAuthenticationUseCase refreshAuthenticationUseCase;
    private final LogoutUserUseCase logoutUserUseCase;

    @Override
    @PostMapping("/login")
    public AuthenticationWrapper login(@RequestBody AuthenticateUserCommand command) {
        return this.authenticateUserUseCase.execute(command);
    }

    @Override
    @PostMapping("/refresh")
    public AuthenticationWrapper refresh(@Valid @RequestBody RefreshAuthenticationCommand command) {
        return this.refreshAuthenticationUseCase.execute(command);
    }

    @Override
    @PostMapping("/logout")
    @Authentication(minimumRole = UserRole.CUSTOMER)
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                       @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        this.logoutUserUseCase.execute(LogoutUserCommand.of(authorizationHeader, authenticatedUser));
    }

}
