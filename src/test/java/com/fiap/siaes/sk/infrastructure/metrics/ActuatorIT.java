package com.fiap.siaes.sk.infrastructure.metrics;

import com.fiap.siaes.sk.infrastructure.config.AsyncConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "management.server.port=0")
@ActiveProfiles({"dev", "it"})
@DisplayName("Integration Test - Actuator")
class ActuatorIT {

    @LocalServerPort
    private int serverPort;

    @LocalManagementPort
    private int managementPort;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @TestBean(name = AsyncConfig.DOMAIN_EVENT_EXECUTOR)
    private Executor domainEventExecutor;

    private RestClient restClient;

    static Executor domainEventExecutor() {
        return new SyncTaskExecutor();
    }

    @BeforeEach
    void setUp() {
        this.restClient = RestClient.builder()
                .defaultStatusHandler(_ -> true, (_, _) -> { })
                .build();
    }

    @Test
    @DisplayName("[200] Deve expor as métricas no formato Prometheus na porta de gerenciamento")
    void shouldExposePrometheusMetricsOnManagementPort() {
        this.post(this.serverPort, "/api/v1/auth/login", "{}");

        ResponseEntity<String> response = this.get(this.managementPort, "/actuator/prometheus");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("http_server_requests_seconds"));
        assertTrue(response.getBody().contains("application=\"siaes\""));
    }

    @Test
    @DisplayName("[200] Deve responder a liveness na porta de gerenciamento")
    void shouldExposeLivenessOnManagementPort() {
        ResponseEntity<String> response = this.get(this.managementPort, "/actuator/health/liveness");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"status\":\"UP\""));
    }

    @Test
    @DisplayName("Não deve expor as métricas na porta da aplicação")
    void shouldNotExposeMetricsOnApplicationPort() {
        ResponseEntity<String> response = this.get(this.serverPort, "/actuator/prometheus");

        assertNotEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("[404] Não deve expor endpoints do Actuator fora da lista mínima")
    void shouldNotExposeSensitiveEndpoints() {
        ResponseEntity<String> response = this.get(this.managementPort, "/actuator/env");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private ResponseEntity<String> get(int port, String path) {
        return this.restClient.get()
                .uri("http://localhost:" + port + path)
                .retrieve()
                .toEntity(String.class);
    }

    private void post(int port, String path, String body) {
        this.restClient.post()
                .uri("http://localhost:" + port + path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
