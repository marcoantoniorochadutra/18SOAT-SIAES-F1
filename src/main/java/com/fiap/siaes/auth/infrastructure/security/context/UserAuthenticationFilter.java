package com.fiap.siaes.auth.infrastructure.security.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.siaes.auth.domain.exception.BlockedTokenException;
import com.fiap.siaes.auth.domain.exception.InsufficientRoleException;
import com.fiap.siaes.auth.domain.exception.InvalidTokenException;
import com.fiap.siaes.auth.domain.security.TokenBlacklist;
import com.fiap.siaes.auth.domain.security.TokenIssuer.UserClaim;
import com.fiap.siaes.auth.infrastructure.security.authorization.Authentication;
import com.fiap.siaes.auth.infrastructure.security.authorization.BearerTokenExtractor;
import com.fiap.siaes.auth.infrastructure.security.jwt.JwtProperties;
import com.fiap.siaes.sk.domain.exception.DomainException;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.repository.UserRepository;
import com.fiap.siaes.user.infrastructure.persistence.projection.UserAuthenticationProjection;
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
import java.util.Set;

import static com.fiap.siaes.auth.domain.security.TokenIssuer.USER_DATA_CLAIM;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final TokenBlacklist tokenBlocklist;
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

        Authentication requiredAuthentication = this.resolveAuthenticationAnnotation(request);

        if (isNull(requiredAuthentication)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UserAuthenticationProjection user = this.authenticate(token, requiredAuthentication.minimumRole());

            this.setAuthenticatedUserContext(user);

            filterChain.doFilter(request, response);
        } catch (DomainException exception) {
            this.handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    private void setAuthenticatedUserContext(UserAuthenticationProjection user) {
        var authority = new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name());
        AuthenticatedUser authenticatedUser = AuthenticatedUser.from(user);

        var authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, Set.of(authority));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserAuthenticationProjection authenticate(String token, UserRole minimumRole) {
        if (isNull(token)) {
            throw new InvalidTokenException();
        }

        UserAuthenticationProjection user = this.findUserFromToken(token);

        if (user.getUserRole().lacksAccessTo(minimumRole)) {
            throw new InsufficientRoleException(minimumRole);
        }

        return user;
    }

    private UserAuthenticationProjection findUserFromToken(String token) {
        UserId userId = this.extractUserId(token);

        var authUser = this.userRepository.findUserAuthById(userId);

        if (isNull(authUser))
            throw new InvalidTokenException();

        return authUser;
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

    private Authentication resolveAuthenticationAnnotation(HttpServletRequest request) {
        HandlerMethod handlerMethod = this.resolveHandlerMethod(request);

        if (isNull(handlerMethod)) {
            return null;
        }

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
