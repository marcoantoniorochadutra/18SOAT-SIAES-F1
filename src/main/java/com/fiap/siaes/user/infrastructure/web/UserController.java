package com.fiap.siaes.user.infrastructure.web;

import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.user.application.usecase.CreateUserUseCase;
import com.fiap.siaes.user.application.usecase.CreateUserUseCase.CreateUserCommand;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.infrastructure.web.openapi.UserControllerOpenApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.fiap.siaes.sk.util.ControllerUtils.createdResponse;

@RestController
@RequestMapping(UserController.ENDPOINT)
@RequiredArgsConstructor
@Authentication(minimumRole = UserRole.MANAGER)
public class UserController implements UserControllerOpenApi {

    public static final String ENDPOINT = "/api/v1/users";

    private final CreateUserUseCase createUserUseCase;

    @Override
    @PostMapping
    public ResponseEntity<UserId> create(@Valid @RequestBody CreateUserCommand command,
                                         @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        UserId id = this.createUserUseCase.execute(command.with(authenticatedUser));
        return createdResponse(ENDPOINT, id);
    }
}
