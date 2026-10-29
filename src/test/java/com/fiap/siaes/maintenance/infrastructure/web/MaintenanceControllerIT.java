package com.fiap.siaes.maintenance.infrastructure.web;

import com.fiap.siaes.maintenance.MaintenanceTestFactory;
import com.fiap.siaes.maintenance.application.usecase.CreateMaintenanceUseCase.CreateMaintenanceCommand;
import com.fiap.siaes.maintenance.application.usecase.UpdateMaintenanceUseCase.UpdateMaintenanceCommand;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.utils.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.fiap.siaes.utils.TestFactory.persist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Test - Maintenance")
class MaintenanceControllerIT extends IntegrationTestUtils {

    private String managerToken;

    @BeforeEach
    void setUp() {
        this.managerToken = super.loginAs(UserRole.MANAGER);
    }

    @Test
    @DisplayName("[201] Deve criar serviço")
    void shouldCreateMaintenance() throws Exception {
        var command = CreateMaintenanceCommand.of("ALINHAMENTO", "Alinhamento e balanceamento", new BigDecimal("120.00"), 60);

        var id = super.createdId(super.executePost(MaintenanceController.ENDPOINT, this.managerToken, command));

        var maintenance = super.maintenanceRepository.findByIdOrThrowNotFound(new MaintenanceId(id));
        assertEquals("ALINHAMENTO", maintenance.getCode().getValue());
        assertTrue(maintenance.isActive());
    }

    @Test
    @DisplayName("[200] Deve buscar serviço")
    void shouldGetMaintenance() throws Exception {
        var maintenance = this.persistMaintenance();

        super.executeGet(this.url(maintenance), this.managerToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.id", maintenance.getId().toString()))
                .andExpect(jsonValue("$.code", maintenance.getCode().getValue()))
                .andExpect(jsonValue("$.active", true));
    }

    @Test
    @DisplayName("[200] Deve atualizar serviço")
    void shouldUpdateMaintenance() throws Exception {
        var maintenance = this.persistMaintenance();
        var command = UpdateMaintenanceCommand.builder()
                .code(MaintenanceTestFactory.CODE)
                .description("Troca de óleo sintético")
                .price(new BigDecimal("150.00"))
                .estimatedDurationMinutes(90)
                .build();

        super.executePut(this.url(maintenance), this.managerToken, command)
                .andExpect(status().isOk());

        var updated = super.maintenanceRepository.findByIdOrThrowNotFound(maintenance.getId());
        assertEquals("Troca de óleo sintético", updated.getDescription());
        assertEquals(90, updated.getEstimatedDurationMinutes());
    }

    @Test
    @DisplayName("[200] Deve listar serviços")
    void shouldListMaintenances() throws Exception {
        var maintenance = this.persistMaintenance();

        super.executeGet(MaintenanceController.ENDPOINT, this.managerToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.content.length()", 1))
                .andExpect(jsonValue("$.content[0].id", maintenance.getId().toString()));
    }

    @Test
    @DisplayName("[204] Deve desativar e reativar serviço")
    void shouldDeactivateAndActivateMaintenance() throws Exception {
        var maintenance = this.persistMaintenance();

        super.executePatch(this.url(maintenance) + "/deactivate", this.managerToken, null)
                .andExpect(status().isNoContent());
        assertFalse(super.maintenanceRepository.findByIdOrThrowNotFound(maintenance.getId()).isActive());

        super.executePatch(this.url(maintenance) + "/activate", this.managerToken, null)
                .andExpect(status().isNoContent());
        assertTrue(super.maintenanceRepository.findByIdOrThrowNotFound(maintenance.getId()).isActive());
    }

    @Test
    @DisplayName("[403] Mecânico não deve listar serviços")
    void shouldNotListMaintenancesAsMechanic() throws Exception {
        var mechanicToken = super.loginAs(UserRole.MECHANIC);

        super.executeGet(MaintenanceController.ENDPOINT, mechanicToken)
                .andExpect(status().isForbidden());
    }

    private Maintenance persistMaintenance() {
        return persist(super.maintenanceRepository, MaintenanceTestFactory.createMaintenance());
    }

    private String url(Maintenance maintenance) {
        return MaintenanceController.ENDPOINT + "/" + maintenance.getId();
    }
}
