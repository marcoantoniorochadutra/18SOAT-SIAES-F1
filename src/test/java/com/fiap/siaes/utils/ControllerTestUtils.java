package com.fiap.siaes.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.siaes.auth.infrastructure.security.context.AuthenticatedUser;
import com.fiap.siaes.auth.infrastructure.security.context.UserAuthenticationFilter;
import com.fiap.siaes.sk.infraestructure.config.I18nConfig;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.utils.ControllerTestUtils.SecurityArgumentResolverConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@Import({I18nConfig.class, SecurityArgumentResolverConfig.class})
public abstract class ControllerTestUtils {

    protected static final AuthenticatedUser AUTHENTICATED_USER_ADMIN = UserTestFactory.authenticatedUserAdmin();

    @Autowired
    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected MessageSource i18nValidationMessageSource;

    @MockitoBean
    private UserAuthenticationFilter userAuthenticationFilter;



    public ResultActions executeGet(String url) throws Exception {
        return this.mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .locale(LocaleContextHolder.getLocale()));
    }

    public ResultActions executeGetAuthenticated(String url) throws Exception {
        return this.executeGetAuthenticated(url, AUTHENTICATED_USER_ADMIN);
    }

    public ResultActions executeGetAuthenticated(String url, AuthenticatedUser authenticatedUser) throws Exception {
        this.authenticateAs(authenticatedUser);
        return this.executeGet(url);
    }

    public ResultActions executePostAuthenticated(String url, Object body) throws Exception {
        return this.executePostAuthenticated(url, body, AUTHENTICATED_USER_ADMIN);
    }

    public ResultActions executePostAuthenticated(String url, Object body,AuthenticatedUser authenticatedUser) throws Exception {
        this.authenticateAs(authenticatedUser);
        return this.executePost(url, body);
    }

    public ResultActions executePutAuthenticated(String url, Object body) throws Exception {
        return this.executePutAuthenticated(url, body, AUTHENTICATED_USER_ADMIN);
    }

    public ResultActions executePutAuthenticated(String url, Object body, AuthenticatedUser authenticatedUser) throws Exception {
        this.authenticateAs(authenticatedUser);
        return this.executePut(url, body);
    }

    public ResultActions executePost(String url, Object body) throws Exception {
        return this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .locale(LocaleContextHolder.getLocale())
                .content(this.objectMapper.writeValueAsString(body)));
    }

    public ResultActions executeDelete(String url) throws Exception {
        return this.mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .locale(LocaleContextHolder.getLocale()));
    }

    public ResultActions executePut(String url, Object body) throws Exception {
        return this.mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .locale(LocaleContextHolder.getLocale())
                .content(this.objectMapper.writeValueAsString(body)));
    }

    protected void authenticateAs(AuthenticatedUser authenticatedUser) {
        var authority = new SimpleGrantedAuthority("ROLE_" + authenticatedUser.userRole().name());
        var authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, Set.of(authority));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public ResultMatcher matchValidationMessage(String code, Object... args) {
        var hasSpecialTreatment = this.hasSpecialTreatment(code);

        var message = this.searchMessage(code, hasSpecialTreatment, args);

        if (isTrue(hasSpecialTreatment)) {
            message = this.updateForMinSizeValidation(message, args);
            message = this.updateForMaxSizeValidation(message, args);
            message = this.updateForMinValueValidation(message, args);
        }

        return content().string(containsString(message));
    }

    private boolean hasSpecialTreatment(String code) {
        return code.contains("Min") || code.contains("Max");
    }

    private String searchMessage(String code, boolean hasSpecialTreatment, Object[] args) {
        if (isFalse(hasSpecialTreatment))
            return this.i18nValidationMessageSource.getMessage(code, args, LocaleContextHolder.getLocale());

        return this.i18nValidationMessageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    private String updateForMaxSizeValidation(String message, Object[] args) {
        message = message.replace("{max}", String.valueOf(args[0]));
        return message;
    }

    private String updateForMinValueValidation(String message, Object[] args) {
        message = message.replace("{value}", String.valueOf(args[0]));
        return message;
    }

    private String updateForMinSizeValidation(String message, Object[] args) {
        message = message.replace("{min}", String.valueOf(args[0]));
        return message;
    }

    @TestConfiguration
    static class SecurityArgumentResolverConfig implements WebMvcConfigurer {

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new AuthenticationPrincipalArgumentResolver());
        }
    }
}
