package com.fiap.siaes.auth.infrastructure.config;

import com.fiap.siaes.auth.infrastructure.security.context.UserAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] SWAGGER_ENDPOINTS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml"
    };

    private final UserAuthenticationFilter userAuthenticationFilter;

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerFilterChain(HttpSecurity http) {
        return this.statelessHttp(http)
                .securityMatcher(SWAGGER_ENDPOINTS)
                .authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return this.statelessHttp(http)
                .authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
                .addFilterBefore(this.userAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private HttpSecurity statelessHttp(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    @Bean
    public FilterRegistrationBean<UserAuthenticationFilter> disableAutomaticFilterRegistration() {
        var registration = new FilterRegistrationBean<>(this.userAuthenticationFilter);
        registration.setEnabled(false);
        return registration;
    }
}
