package com.fiap.siaes.auth.infrastructure.web.openapi;

import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticateUserCommand;
import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticationWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "auth.tag.description")
public interface AuthenticationControllerOpenApi {

    @Operation(summary = "auth.login.summary", description = "auth.login.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "200", description = "auth.login.response.success"),
                     @ApiResponse(responseCode = "401", description = "auth.login.response.unauthorized"),
                     @ApiResponse(responseCode = "404", description = "auth.login.response.notfound"),
    })
    AuthenticationWrapper login(AuthenticateUserCommand command);

    @Operation(summary = "auth.logout.summary", description = "auth.logout.description", method = "POST",
               responses = {
                     @ApiResponse(responseCode = "200", description = "auth.logout.response.success"),
                     @ApiResponse(responseCode = "400", description = "auth.logout.response.badrequest"),
    })
    void logout(String authorizationHeader);

}
