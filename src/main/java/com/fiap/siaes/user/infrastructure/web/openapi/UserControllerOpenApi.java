package com.fiap.siaes.user.infrastructure.web.openapi;

import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.user.application.usecase.CreateUserUseCase.CreateUserCommand;
import com.fiap.siaes.user.domain.model.vo.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Users", description = "user.tag.description")
public interface UserControllerOpenApi {

    @Operation(summary = "user.create.summary", description = "user.create.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "201", description = "user.create.response.success",
                                  headers = @Header(name = "Location")),
                     @ApiResponse(responseCode = "400", description = "user.create.response.badrequest"),
                     @ApiResponse(responseCode = "403", description = "user.create.response.forbidden"),
                     @ApiResponse(responseCode = "409", description = "user.create.response.conflict"),
    })
    ResponseEntity<UserId> create(CreateUserCommand command, AuthenticatedUser authenticatedUser);

}
