package com.fiap.siaes.user.infrastructure.web;

import com.fiap.siaes.user.application.usecase.CreateUserUseCase;
import com.fiap.siaes.user.application.usecase.CreateUserUseCase.CreateUserCommand;
import com.fiap.siaes.user.domain.model.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.fiap.siaes.sk.util.ControllerUtils.createdResponse;

@Tag(name = "Users", description = "Gestão de usuários")
@RestController
@RequestMapping(UserController.ENDPOINT)
@RequiredArgsConstructor
public class UserController {

    public static final String ENDPOINT = "/api/v1/users";

    private final CreateUserUseCase createUserUseCase;

    @Operation(summary = "Cria um novo usuário")
    @ApiResponse(responseCode = "201", description = "Usuário criado")
    @PostMapping
    public ResponseEntity<UserId> create(@RequestBody CreateUserCommand command) {
        UserId id = this.createUserUseCase.execute(command);
        return createdResponse(ENDPOINT, id);
    }
}
