package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.domain.exception.MaintenanceAlreadyInactiveException;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Deactivate Maintenance")
class DeactivateMaintenanceAppServiceTest {

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @InjectMocks
    private DeactivateMaintenanceAppService deactivateMaintenanceAppService;

    @Test
    @DisplayName("Deve inativar um serviço com sucesso")
    void shouldDeactivateMaintenanceSuccessfully() {
        var maintenance = createMaintenance(true);

        when(this.maintenanceRepository.findByIdOrThrowNotFound(maintenance.getId())).thenReturn(maintenance);

        this.deactivateMaintenanceAppService.execute(maintenance.getId());

        Maintenance savedMaintenance = captureSave(this.maintenanceRepository, Maintenance.class);

        assertFalse(savedMaintenance.isActive());
    }

    @Test
    @DisplayName("Não deve inativar serviço já inativo")
    void shouldNotDeactivateWhenAlreadyInactive() {
        var maintenance = createMaintenance(false);
        var maintenanceId = maintenance.getId();

        when(this.maintenanceRepository.findByIdOrThrowNotFound(maintenanceId)).thenReturn(maintenance);

        var exception = assertThrows(MaintenanceAlreadyInactiveException.class,
                () -> this.deactivateMaintenanceAppService.execute(maintenanceId));

        assertExceptionParameters(exception, maintenance.getId().toString());
        verify(this.maintenanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve inativar serviço inexistente")
    void shouldNotDeactivateWhenNotFound() {
        var id = MaintenanceId.generate();

        when(this.maintenanceRepository.findByIdOrThrowNotFound(id)).thenThrow(new MaintenanceNotFoundException(id.toString()));

        var exception = assertThrows(MaintenanceNotFoundException.class,
                () -> this.deactivateMaintenanceAppService.execute(id));

        assertExceptionParameters(exception, id.toString());
        verify(this.maintenanceRepository, never()).save(any());
    }
}
