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
import com.fiap.siaes.auth.infrastructure.security.authorization.BearerTokenExtractor;
import com.fiap.siaes.auth.infrastructure.security.authorization.PublicEndpoint;
import com.fiap.siaes.auth.infrastructure.security.jwt.JwtProperties;
import com.fiap.siaes.auth.infrastructure.security.jwt.JwtTokenIssuer.UserClaim;
import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.user.domain.model.UserCredentials;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static com.fiap.siaes.auth.infrastructure.security.jwt.JwtTokenIssuer.USER_DATA_CLAIM;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final TokenBlocklist tokenBlocklist;
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = BearerTokenExtractor.extract(request.getHeader(HttpHeaders.AUTHORIZATION));

        if (nonNull(token) && this.tokenBlocklist.isBlocked(token)) {
            this.handlerExceptionResolver.resolveException(request, response, null, new BlockedTokenException());
            return;
        }

        HandlerMethod handlerMethod = this.resolveHandlerMethod(request);

        if (isNull(handlerMethod) || this.isPublic(handlerMethod)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication requiredAuthentication = this.resolveAuthenticationAnnotation(handlerMethod);

        try {
            UserCredentials user = this.authenticate(token, requiredAuthentication);

            this.setAuthenticatedUserContext(user);

            filterChain.doFilter(request, response);
        } catch (DomainException exception) {
            this.handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    private void setAuthenticatedUserContext(UserCredentials user) {
        var authority = new SimpleGrantedAuthority("ROLE_" + user.role().name());
        AuthenticatedUser authenticatedUser = this.toAuthenticatedUser(user);

        var authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, Set.of(authority));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private AuthenticatedUser toAuthenticatedUser(UserCredentials user) {
        return AuthenticatedUser.of(user.id(), user.customerId(), user.email(), user.role());
    }

    private UserCredentials authenticate(String token, Authentication requiredAuthentication) {
        if (isNull(requiredAuthentication)) {
            throw new UndeclaredEndpointAccessException();
        }

        if (isNull(token)) {
            throw new InvalidTokenException();
        }

        UserCredentials user = this.findUserFromToken(token);

        if (user.role().lacksAccessTo(requiredAuthentication.minimumRole())) {
            throw new InsufficientRoleException(requiredAuthentication.minimumRole());
        }

        if (Arrays.asList(requiredAuthentication.deniedRoles()).contains(user.role())) {
            throw new RoleNotAllowedException(user.role());
        }

        return user;
    }

    private UserCredentials findUserFromToken(String token) {
        UserId userId = this.extractUserId(token);

        return this.userRepository.findCredentialsById(userId)
                .orElseThrow(InvalidTokenException::new);
    }

    private UserId extractUserId(String token) {
        try {
            var userClaim = Jwts.parser()
                    .verifyWith(this.jwtProperties.getTokenSecret())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(USER_DATA_CLAIM);


            UserClaim userData = this.objectMapper.convertValue(userClaim, UserClaim.class);

            if (isNull(userData)) {
                throw new InvalidTokenException();
            }

            return userData.userId();
        } catch (JwtException | IllegalArgumentException _) {
            throw new InvalidTokenException();
        }
    }

    private boolean isPublic(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(PublicEndpoint.class)
                || handlerMethod.getBeanType().isAnnotationPresent(PublicEndpoint.class);
    }

    private Authentication resolveAuthenticationAnnotation(HandlerMethod handlerMethod) {
        Authentication methodAuthentication = handlerMethod.getMethodAnnotation(Authentication.class);

        if (nonNull(methodAuthentication)) {
            return methodAuthentication;
        }

        return handlerMethod.getBeanType().getAnnotation(Authentication.class);
    }

    private HandlerMethod resolveHandlerMethod(HttpServletRequest request) {
        try {
            HandlerExecutionChain handlerChain = this.requestMappingHandlerMapping.getHandler(request);

            if (nonNull(handlerChain) && handlerChain.getHandler() instanceof HandlerMethod handlerMethod) {
                return handlerMethod;
            }

            return null;
        } catch (Exception _) {
            return null;
        }
    }
}
