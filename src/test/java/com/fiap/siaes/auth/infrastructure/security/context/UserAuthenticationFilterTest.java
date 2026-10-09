package com.fiap.siaes.auth.infrastructure.security.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.siaes.auth.domain.exception.BlockedTokenException;
import com.fiap.siaes.auth.domain.exception.InsufficientRoleException;
import com.fiap.siaes.auth.domain.exception.InvalidTokenException;
import com.fiap.siaes.auth.domain.security.TokenBlacklist;
import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.auth.infrastructure.security.jwt.JwtProperties;
import com.fiap.siaes.auth.infrastructure.security.jwt.JwtTokenIssuer;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.repository.UserRepository;
import com.fiap.siaes.user.infrastructure.persistence.projection.UserAuthenticationProjection;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Infrastructure - User Authentication Filter")
class UserAuthenticationFilterTest {

    private static final String TOKEN = "access-token";
    private static final UserId USER_ID = UserId.generate();

    @Mock
    private TokenBlacklist tokenBlocklist;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private UserAuthenticationProjection userAuthenticationProjection;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtTokenIssuer jwtTokenIssuer;

    private UserAuthenticationFilter userAuthenticationFilter;

    @BeforeEach
    void setUp() {
        var jwtProperties = new JwtProperties("test-secret-key-at-least-32-bytes-long!!", 1, 1);
        this.jwtTokenIssuer = new JwtTokenIssuer(jwtProperties, new SecureRandom());
        this.userAuthenticationFilter = new UserAuthenticationFilter(
                this.tokenBlocklist, new ObjectMapper(), jwtProperties,
                this.userRepository, this.requestMappingHandlerMapping, this.handlerExceptionResolver);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve seguir a cadeia de filtros quando o endpoint não exige autenticação")
    void shouldContinueTheChainWhenEndpointHasNoAuthenticationAnnotation() throws Exception {
        when(this.request.getHeader("Authorization")).thenReturn(null);

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.filterChain).doFilter(this.request, this.response);
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
        when(this.userAuthenticationProjection.getId()).thenReturn(USER_ID.id());
        when(this.userAuthenticationProjection.getEmail()).thenReturn("maria@email.com");
        when(this.userAuthenticationProjection.getUserRole()).thenReturn(UserRole.MANAGER);
        when(this.userRepository.findUserAuthById(USER_ID)).thenReturn(this.userAuthenticationProjection);

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
        when(this.userAuthenticationProjection.getId()).thenReturn(USER_ID.id());
        when(this.userAuthenticationProjection.getEmail()).thenReturn("maria@email.com");
        when(this.userAuthenticationProjection.getUserRole()).thenReturn(UserRole.MANAGER);
        when(this.userRepository.findUserAuthById(USER_ID)).thenReturn(this.userAuthenticationProjection);

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.filterChain).doFilter(this.request, this.response);
    }

    @Test
    @DisplayName("Deve rejeitar com 403 quando a role do usuário não atinge a role mínima exigida")
    void shouldRejectWithForbiddenWhenRoleIsInsufficient() throws Exception {
        String token = this.jwtTokenIssuer.issueAccessToken(USER_ID, UserRole.CUSTOMER);

        this.mockHandlerMethod(ClassLevelController.class, "handle");
        when(this.request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(this.userAuthenticationProjection.getUserRole()).thenReturn(UserRole.CUSTOMER);
        when(this.userRepository.findUserAuthById(USER_ID)).thenReturn(this.userAuthenticationProjection);

        this.userAuthenticationFilter.doFilter(this.request, this.response, this.filterChain);

        verify(this.handlerExceptionResolver)
                .resolveException(eq(this.request), eq(this.response), isNull(), isA(InsufficientRoleException.class));
        verify(this.filterChain, never()).doFilter(this.request, this.response);
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
        when(this.userRepository.findUserAuthById(USER_ID)).thenReturn(null);

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

    private void mockHandlerMethod(Class<?> controllerType, String methodName) throws Exception {
        Object bean = controllerType.getDeclaredConstructor().newInstance();
        HandlerMethod handlerMethod = new HandlerMethod(bean, controllerType.getMethod(methodName));
        when(this.requestMappingHandlerMapping.getHandler(this.request))
                .thenReturn(new HandlerExecutionChain(handlerMethod));
    }

    @Authentication(minimumRole = UserRole.MANAGER)
    static class ClassLevelController {
        public void handle() {}
    }

    @Authentication(minimumRole = UserRole.ADMIN)
    static class MethodOverridesClassController {
        @Authentication(minimumRole = UserRole.MANAGER)
        public void handle() {}
    }
}
