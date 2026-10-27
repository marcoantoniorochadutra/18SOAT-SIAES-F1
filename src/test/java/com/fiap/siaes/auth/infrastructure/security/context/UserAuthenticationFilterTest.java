package com.fiap.siaes.auth.infrastructure.security.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.auth.domain.exception.BlockedTokenException;
import com.fiap.siaes.auth.domain.exception.InsufficientRoleException;
import com.fiap.siaes.auth.domain.exception.InvalidTokenException;
import com.fiap.siaes.auth.domain.exception.RoleNotAllowedException;
import com.fiap.siaes.auth.domain.exception.UndeclaredEndpointAccessException;
import com.fiap.siaes.auth.domain.security.TokenBlocklist;
import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.auth.infrastructure.security.authorization.PublicEndpoint;
import com.fiap.siaes.auth.infrastructure.security.jwt.JwtProperties;
import com.fiap.siaes.auth.infrastructure.security.jwt.JwtTokenIssuer;
import com.fiap.siaes.user.domain.model.UserCredentials;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.security.SecureRandom;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Infrastructure - User Authentication Filter")
class UserAuthenticationFilterTest {

    private static final String TOKEN = "access-token";
    private static final String EMAIL = "maria@email.com";
    private static final UserId USER_ID = UserId.generate();
    private static final JwtProperties JWT_PROPERTIES = new JwtProperties("test-secret-key-at-least-32-bytes-long!!", 1, 1);

    @Mock
    private TokenBlocklist tokenBlocklist;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Spy
    private JwtProperties jwtProperties = JWT_PROPERTIES;

    @InjectMocks
    private UserAuthenticationFilter userAuthenticationFilter;

    private final JwtTokenIssuer jwtTokenIssuer = new JwtTokenIssuer(JWT_PROPERTIES, new SecureRandom());

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve seguir a cadeia de filtros quando a requisição não corresponde a um endpoint")
    void shouldContinueTheChainWhenRequestHasNoHandlerMethod() throws Exception {
        when(this.request.getHeader("Authorization")).thenReturn(null);

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.filterChain).doFilter(this.request, this.response);
    }

    @Test
    @DisplayName("Deve seguir a cadeia de filtros quando o endpoint é público")
    void shouldContinueTheChainWhenEndpointIsPublic() throws Exception {
        this.mockHandlerMethod(PublicController.class, "handle");
        when(this.request.getHeader("Authorization")).thenReturn(null);

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.filterChain).doFilter(this.request, this.response);
    }

    @Test
    @DisplayName("Deve rejeitar com 401 quando o endpoint não declara nenhuma anotação de acesso")
    void shouldRejectWithUnauthorizedWhenEndpointHasNoAnnotation() throws Exception {
        this.mockHandlerMethod(UnannotatedController.class, "handle");
        when(this.request.getHeader("Authorization")).thenReturn(null);

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.handlerExceptionResolver)
                .resolveException(eq(this.request), eq(this.response), isNull(), isA(UndeclaredEndpointAccessException.class));
        verify(this.filterChain, never()).doFilter(this.request, this.response);
    }

    @Test
    @DisplayName("Deve rejeitar com 401 o endpoint sem anotação de acesso mesmo com token válido")
    void shouldRejectWithUnauthorizedWhenEndpointHasNoAnnotationEvenWithValidToken() throws Exception {
        String token = this.jwtTokenIssuer.issueAccessToken(USER_ID, UserRole.ADMIN);

        this.mockHandlerMethod(UnannotatedController.class, "handle");
        when(this.request.getHeader("Authorization")).thenReturn("Bearer " + token);

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.handlerExceptionResolver)
                .resolveException(eq(this.request), eq(this.response), isNull(), isA(UndeclaredEndpointAccessException.class));
        verify(this.filterChain, never()).doFilter(this.request, this.response);
        verifyNoInteractions(this.userRepository);
    }

    @Test
    @DisplayName("Deve rejeitar com 401 quando o token informado está na blocklist")
    void shouldRejectWithUnauthorizedWhenTokenIsBlocked() throws Exception {
        when(this.request.getHeader("Authorization")).thenReturn("Bearer " + TOKEN);
        when(this.tokenBlocklist.isBlocked(TOKEN)).thenReturn(true);

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.handlerExceptionResolver)
                .resolveException(eq(this.request), eq(this.response), isNull(), isA(BlockedTokenException.class));
        verify(this.filterChain, never()).doFilter(this.request, this.response);
    }

    @Test
    @DisplayName("Deve autenticar com sucesso usando a role atual do usuário e a anotação de classe")
    void shouldAuthenticateUsingClassLevelAnnotation() throws Exception {
        String token = this.jwtTokenIssuer.issueAccessToken(USER_ID, UserRole.MANAGER);

        this.mockHandlerMethod(ClassLevelController.class, "handle");
        when(this.request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(this.userRepository.findCredentialsById(USER_ID)).thenReturn(Optional.of(credentials("maria@email.com", UserRole.MANAGER)));

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.filterChain).doFilter(this.request, this.response);
        var principal = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(USER_ID, principal.id());
        assertEquals("maria@email.com", principal.email());
        assertEquals(UserRole.MANAGER, principal.userRole());
    }

    @Test
    @DisplayName("A anotação do método deve sobrescrever a anotação da classe")
    void shouldLetMethodAnnotationOverrideClassAnnotation() throws Exception {
        String token = this.jwtTokenIssuer.issueAccessToken(USER_ID, UserRole.MANAGER);

        this.mockHandlerMethod(MethodOverridesClassController.class, "handle");
        when(this.request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(this.userRepository.findCredentialsById(USER_ID)).thenReturn(Optional.of(credentials("maria@email.com", UserRole.MANAGER)));

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.filterChain).doFilter(this.request, this.response);
    }

    @Test
    @DisplayName("Deve rejeitar com 403 quando a role do usuário não atinge a role mínima exigida")
    void shouldRejectWithForbiddenWhenRoleIsInsufficient() throws Exception {
        String token = this.jwtTokenIssuer.issueAccessToken(USER_ID, UserRole.CUSTOMER);

        this.mockHandlerMethod(ClassLevelController.class, "handle");
        when(this.request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(this.userRepository.findCredentialsById(USER_ID)).thenReturn(Optional.of(credentials(EMAIL, UserRole.CUSTOMER)));

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.handlerExceptionResolver)
                .resolveException(eq(this.request), eq(this.response), isNull(), isA(InsufficientRoleException.class));
        verify(this.filterChain, never()).doFilter(this.request, this.response);
    }

    @Test
    @DisplayName("Deve rejeitar com 403 quando a role do usuário está na lista de roles negadas")
    void shouldRejectWithForbiddenWhenRoleIsDenied() throws Exception {
        String token = this.jwtTokenIssuer.issueAccessToken(USER_ID, UserRole.MECHANIC);

        this.mockHandlerMethod(DeniedRoleController.class, "handle");
        when(this.request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(this.userRepository.findCredentialsById(USER_ID)).thenReturn(Optional.of(credentials(EMAIL, UserRole.MECHANIC)));

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.handlerExceptionResolver)
                .resolveException(eq(this.request), eq(this.response), isNull(), isA(RoleNotAllowedException.class));
        verify(this.filterChain, never()).doFilter(this.request, this.response);
    }

    @Test
    @DisplayName("Deve seguir a cadeia quando a role do usuário atinge o mínimo e não está na lista de negadas")
    void shouldContinueTheChainWhenRoleIsNotDenied() throws Exception {
        String token = this.jwtTokenIssuer.issueAccessToken(USER_ID, UserRole.CUSTOMER);

        this.mockHandlerMethod(DeniedRoleController.class, "handle");
        when(this.request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(this.userRepository.findCredentialsById(USER_ID)).thenReturn(Optional.of(credentials("joao@email.com", UserRole.CUSTOMER)));

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.filterChain).doFilter(this.request, this.response);
    }

    @Test
    @DisplayName("Deve rejeitar com 401 quando nenhum token é informado em endpoint protegido")
    void shouldRejectWithUnauthorizedWhenTokenIsMissingOnProtectedEndpoint() throws Exception {
        this.mockHandlerMethod(ClassLevelController.class, "handle");
        when(this.request.getHeader("Authorization")).thenReturn(null);

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.handlerExceptionResolver)
                .resolveException(eq(this.request), eq(this.response), isNull(), isA(InvalidTokenException.class));
        verify(this.filterChain, never()).doFilter(this.request, this.response);
    }

    @Test
    @DisplayName("Deve rejeitar com 401 quando o token não corresponde a um usuário existente")
    void shouldRejectWithUnauthorizedWhenUserIsNotFound() throws Exception {
        String token = this.jwtTokenIssuer.issueAccessToken(USER_ID, UserRole.MANAGER);

        this.mockHandlerMethod(ClassLevelController.class, "handle");
        when(this.request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(this.userRepository.findCredentialsById(USER_ID)).thenReturn(Optional.empty());

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.handlerExceptionResolver)
                .resolveException(eq(this.request), eq(this.response), isNull(), isA(InvalidTokenException.class));
        verify(this.filterChain, never()).doFilter(this.request, this.response);
    }

    @Test
    @DisplayName("Deve rejeitar com 401 quando o token informado é inválido")
    void shouldRejectWithUnauthorizedWhenTokenIsMalformed() throws Exception {
        this.mockHandlerMethod(ClassLevelController.class, "handle");
        when(this.request.getHeader("Authorization")).thenReturn("Bearer not-a-jwt");

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.handlerExceptionResolver)
                .resolveException(eq(this.request), eq(this.response), isNull(), isA(InvalidTokenException.class));
        verify(this.filterChain, never()).doFilter(this.request, this.response);
    }

    private UserCredentials credentials(String email, UserRole role) {
        return UserCredentials.builder()
                .id(USER_ID)
                .email(email)
                .password("hashed-password")
                .role(role)
                .build();
    }

    private void mockHandlerMethod(Class<?> controllerType, String methodName) throws Exception {
        Object bean = controllerType.getDeclaredConstructor().newInstance();
        HandlerMethod handlerMethod = new HandlerMethod(bean, controllerType.getMethod(methodName));
        when(this.requestMappingHandlerMapping.getHandler(this.request))
                .thenReturn(new HandlerExecutionChain(handlerMethod));
    }

    @PublicEndpoint
    static class PublicController {
        public void handle() { /* Teste para validar endpoint público */ }
    }

    static class UnannotatedController {
        public void handle() { /* Teste para validar negação por padrão */ }
    }

    @Authentication(minimumRole = UserRole.MANAGER)
    static class ClassLevelController {
        public void handle() { /* Teste para validar autenticação por classe */ }
    }

    @Authentication(minimumRole = UserRole.ADMIN)
    static class MethodOverridesClassController {
        @Authentication(minimumRole = UserRole.MANAGER)
        public void handle() { /* Teste para validar anulação de autenticação por metodo */ }
    }

    @Authentication(minimumRole = UserRole.CUSTOMER, deniedRoles = {UserRole.MECHANIC})
    static class DeniedRoleController {
        public void handle() { /* Teste para validar negação de papel */ }
    }
}
