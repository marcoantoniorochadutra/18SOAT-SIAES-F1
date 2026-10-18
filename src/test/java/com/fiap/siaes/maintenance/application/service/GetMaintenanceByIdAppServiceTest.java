package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Get Maintenance By Id")
class GetMaintenanceByIdAppServiceTest {

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @InjectMocks
    private GetMaintenanceByIdAppService getMaintenanceByIdAppService;

    @Test
    @DisplayName("Deve buscar um serviço pelo id com sucesso")
    void shouldGetMaintenanceSuccessfully() {
        var maintenance = createMaintenance();

        when(this.maintenanceRepository.findByIdOrThrowNotFound(maintenance.getId())).thenReturn(maintenance);

        MaintenanceResponse response = this.getMaintenanceByIdAppService.execute(maintenance.getId());

        assertEquals(maintenance.getId(), response.id());
        assertEquals(maintenance.getCode().getValue(), response.code());
        assertEquals(maintenance.getDescription(), response.description());
        assertEquals(maintenance.getPrice(), response.price());
        assertEquals(maintenance.getEstimatedDurationMinutes(), response.estimatedDurationMinutes());
        assertEquals(maintenance.isActive(), response.active());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o serviço não existir")
    void shouldThrowWhenMaintenanceNotFound() {
        var id = MaintenanceId.generate();

        when(this.maintenanceRepository.findByIdOrThrowNotFound(id)).thenThrow(new MaintenanceNotFoundException(id.toString()));

        var exception = assertThrows(MaintenanceNotFoundException.class,
                () -> this.getMaintenanceByIdAppService.execute(id));

        assertExceptionParameters(exception, id.toString());
    }
}
