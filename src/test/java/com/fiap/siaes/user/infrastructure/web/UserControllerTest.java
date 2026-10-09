package com.fiap.siaes.user.infrastructure.web;

import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.user.application.usecase.CreateUserUseCase;
import com.fiap.siaes.user.application.usecase.CreateUserUseCase.CreateUserCommand;
import com.fiap.siaes.user.domain.exception.UserCustomerRelationRequiredException;
import com.fiap.siaes.user.domain.exception.UserEmailAlreadyExistsException;
import com.fiap.siaes.user.domain.exception.UserEmailOrCustomerRequiredException;
import com.fiap.siaes.user.domain.exception.UserRoleNotAllowedException;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.utils.ControllerTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DisplayName("Controller Test - User")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest extends ControllerTestUtils {

    private static final String EMAIL = "maria@email.com";
    private static final String PASSWORD = "S3nhaForte!";

    @MockitoBean
    private CreateUserUseCase createUserUseCase;


    @Test
    @DisplayName("[201] Deve criar usuário com sucesso")
    void shouldCreateUser() throws Exception {
        var command = CreateUserCommand.builder()
                .name("Maria Silva")
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.MANAGER)
                .authenticatedUser(AUTHENTICATED_USER_ADMIN)
                .build();

        var userId = UserId.generate();

        when(this.createUserUseCase.execute(command)).thenReturn(userId);

        super.executePostAuthenticated(UserController.ENDPOINT, command)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(userId.toString())));

        verify(this.createUserUseCase).execute(command);
    }

    @Test
    @DisplayName("[400] Não deve criar usuário CUSTOMER sem vínculo com um cliente")
    void shouldNotCreateCustomerUserWithoutCustomerId() throws Exception {
        var command = CreateUserCommand.builder()
                .name("Maria Silva")
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.CUSTOMER)
                .authenticatedUser(AUTHENTICATED_USER_ADMIN)
                .build();

        when(this.createUserUseCase.execute(command))
                .thenThrow(new UserCustomerRelationRequiredException());

        super.executePostAuthenticated(UserController.ENDPOINT, command)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[400] Não deve criar usuário sem e-mail e sem vínculo com um cliente")
    void shouldNotCreateUserWithoutEmailOrCustomer() throws Exception {
        var command = CreateUserCommand.builder()
                .name("Maria Silva")
                .password(PASSWORD)
                .role(UserRole.MANAGER)
                .authenticatedUser(AUTHENTICATED_USER_ADMIN)
                .build();

        when(this.createUserUseCase.execute(command))
                .thenThrow(new UserEmailOrCustomerRequiredException());

        super.executePostAuthenticated(UserController.ENDPOINT, command)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[403] Não deve criar usuário com perfil maior do que o do usuário autenticado")
    void shouldNotCreateUserWithRoleHigherThanCurrentUser() throws Exception {
        AuthenticatedUser authenticatedUserCustomer = UserTestFactory.authenticatedUserCustomer();

        var command = CreateUserCommand.builder()
                .name("Maria Silva")
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.ADMIN)
                .authenticatedUser(authenticatedUserCustomer)
                .build();

        when(this.createUserUseCase.execute(command))
                .thenThrow(new UserRoleNotAllowedException(UserRole.ADMIN.name()));

        super.executePostAuthenticated(UserController.ENDPOINT, command, authenticatedUserCustomer)
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[409] Não deve criar usuário com e-mail já cadastrado")
    void shouldNotCreateUserWhenEmailAlreadyExists() throws Exception {
        var command = CreateUserCommand.builder()
                .name("Maria Silva")
                .email(EMAIL)
                .password(PASSWORD)
                .role(UserRole.MANAGER)
                .authenticatedUser(AUTHENTICATED_USER_ADMIN)
                .build();

        when(this.createUserUseCase.execute(command))
                .thenThrow(new UserEmailAlreadyExistsException(EMAIL));

        super.executePostAuthenticated(UserController.ENDPOINT, command)
                .andExpect(status().isConflict());
    }
}
