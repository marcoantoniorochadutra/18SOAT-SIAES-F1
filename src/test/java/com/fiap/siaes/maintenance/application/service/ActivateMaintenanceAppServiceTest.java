package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.domain.exception.MaintenanceAlreadyActiveException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceNotFoundException;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.maintenance.MaintenanceTestFactory.createMaintenance;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Activate Maintenance")
class ActivateMaintenanceAppServiceTest {

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @InjectMocks
    private ActivateMaintenanceAppService activateMaintenanceAppService;

    @Test
    @DisplayName("Deve ativar um serviço com sucesso")
    void shouldActivateMaintenanceSuccessfully() {
        var maintenance = createMaintenance(false);

        when(this.maintenanceRepository.findByIdOrThrowNotFound(maintenance.getId())).thenReturn(maintenance);

        this.activateMaintenanceAppService.execute(maintenance.getId());

        Maintenance savedMaintenance = captureSave(this.maintenanceRepository, Maintenance.class);

        assertTrue(savedMaintenance.isActive());
    }

    @Test
    @DisplayName("Não deve ativar serviço já ativo")
    void shouldNotActivateWhenAlreadyActive() {
        var maintenance = createMaintenance(true);
        var maintenanceId = maintenance.getId();

        when(this.maintenanceRepository.findByIdOrThrowNotFound(maintenanceId)).thenReturn(maintenance);

        var exception = assertThrows(MaintenanceAlreadyActiveException.class,
                () -> this.activateMaintenanceAppService.execute(maintenanceId));

        assertExceptionParameters(exception, maintenanceId.toString());
        verify(this.maintenanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve ativar serviço inexistente")
    void shouldNotActivateWhenNotFound() {
        var id = MaintenanceId.generate();

        when(this.maintenanceRepository.findByIdOrThrowNotFound(id)).thenThrow(new MaintenanceNotFoundException(id.toString()));

        var exception = assertThrows(MaintenanceNotFoundException.class,
                () -> this.activateMaintenanceAppService.execute(id));

        assertExceptionParameters(exception, id.toString());
        verify(this.maintenanceRepository, never()).save(any());
    }
}
