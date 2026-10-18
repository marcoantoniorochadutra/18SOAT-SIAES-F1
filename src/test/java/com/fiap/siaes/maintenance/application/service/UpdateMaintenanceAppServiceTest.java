package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.application.usecase.UpdateMaintenanceUseCase.UpdateMaintenanceCommand;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceNotFoundException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceCodeAlreadyExistsException;
import com.fiap.siaes.maintenance.domain.model.Maintenance;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceCode;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.fiap.siaes.maintenance.MaintenanceTestFactory.createMaintenance;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Update Maintenance")
class UpdateMaintenanceAppServiceTest {

    private static final String NEW_CODE = "ALINHAMENTO";
    private static final BigDecimal NEW_PRICE = new BigDecimal("30.00");
    private static final int NEW_ESTIMATED_DURATION_MINUTES = 45;

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @InjectMocks
    private UpdateMaintenanceAppService updateMaintenanceAppService;

    @Test
    @DisplayName("Deve atualizar um serviço com sucesso")
    void shouldUpdateMaintenanceSuccessfully() {
        var maintenance = createMaintenance();
        var command = this.command(maintenance.getId());

        when(this.maintenanceRepository.findByIdOrThrowNotFound(maintenance.getId())).thenReturn(maintenance);

        var id = this.updateMaintenanceAppService.execute(command);

        Maintenance savedMaintenance = captureSave(this.maintenanceRepository, Maintenance.class);

        assertEquals(maintenance.getId(), id);
        assertEquals(NEW_CODE, savedMaintenance.getCode().getValue());
        assertEquals("Alinhamento", savedMaintenance.getDescription());
        assertEquals(NEW_PRICE, savedMaintenance.getPrice());
        assertEquals(NEW_ESTIMATED_DURATION_MINUTES, savedMaintenance.getEstimatedDurationMinutes());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o serviço a ser atualizado não existir")
    void shouldThrowWhenMaintenanceToUpdateDoesNotExist() {
        var id = MaintenanceId.generate();
        var command = this.command(id);

        when(this.maintenanceRepository.findByIdOrThrowNotFound(id))
                .thenThrow(new MaintenanceNotFoundException(id.toString()));

        var exception = assertThrows(MaintenanceNotFoundException.class,
                () -> this.updateMaintenanceAppService.execute(command));

        assertExceptionParameters(exception, id.toString());
        verify(this.maintenanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve atualizar quando o código já pertencer a outro serviço")
    void shouldNotUpdateWhenCodeAlreadyBelongsToAnotherMaintenance() {
        var maintenance = createMaintenance();
        var command = this.command(maintenance.getId());

        when(this.maintenanceRepository.findByIdOrThrowNotFound(maintenance.getId())).thenReturn(maintenance);
        when(this.maintenanceRepository.existsByCodeAndIdNot(MaintenanceCode.recreate(NEW_CODE), maintenance.getId())).thenReturn(true);

        var exception = assertThrows(MaintenanceCodeAlreadyExistsException.class,
                () -> this.updateMaintenanceAppService.execute(command));

        assertExceptionParameters(exception, NEW_CODE);
        verify(this.maintenanceRepository, never()).save(any());
    }

    private UpdateMaintenanceCommand command(MaintenanceId id) {
        return UpdateMaintenanceCommand.builder()
                .id(id)
                .code("alinhamento")
                .description("Alinhamento")
                .price(NEW_PRICE)
                .estimatedDurationMinutes(NEW_ESTIMATED_DURATION_MINUTES)
                .build();
    }
}
