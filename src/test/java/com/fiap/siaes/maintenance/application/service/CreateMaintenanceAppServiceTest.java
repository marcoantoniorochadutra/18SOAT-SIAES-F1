package com.fiap.siaes.maintenance.application.service;

import com.fiap.siaes.maintenance.application.usecase.CreateMaintenanceUseCase.CreateMaintenanceCommand;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceCodeValueIsNullException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceCodeAlreadyExistsException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceEstimatedDurationInvalidException;
import com.fiap.siaes.maintenance.domain.exception.MaintenancePriceInvalidException;
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

import static com.fiap.siaes.maintenance.MaintenanceTestFactory.DESCRIPTION;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.ESTIMATED_DURATION_MINUTES;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.CODE;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.PRICE;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Create Maintenance")
class CreateMaintenanceAppServiceTest {

    private static final String UNSANITIZED_CODE = " troca-oleo ";

    @Mock
    private MaintenanceRepository maintenanceRepository;


    @InjectMocks
    private CreateMaintenanceAppService createMaintenanceAppService;

    @Test
    @DisplayName("Deve criar um serviço com sucesso")
    void shouldCreateMaintenanceSuccessfully() {
        var command = this.commandBuilder().build();

        MaintenanceId maintenanceId = this.createMaintenanceAppService.execute(command);

        Maintenance savedMaintenance = captureSave(this.maintenanceRepository, Maintenance.class);

        assertEquals(savedMaintenance.getId(), maintenanceId);
        assertEquals(CODE, savedMaintenance.getCode().getValue());
        assertEquals(DESCRIPTION, savedMaintenance.getDescription());
        assertEquals(PRICE, savedMaintenance.getPrice());
        assertEquals(ESTIMATED_DURATION_MINUTES, savedMaintenance.getEstimatedDurationMinutes());
        assertTrue(savedMaintenance.isActive());
    }

    @Test
    @DisplayName("Não deve criar serviço quando o código já estiver cadastrado")
    void shouldNotCreateMaintenanceWhenCodeAlreadyExists() {
        var command = this.commandBuilder().build();

        when(this.maintenanceRepository.existsByCode(MaintenanceCode.recreate(CODE))).thenReturn(true);

        var exception = assertThrows(MaintenanceCodeAlreadyExistsException.class,
                () -> this.createMaintenanceAppService.execute(command));

        assertExceptionParameters(exception, CODE);
        verify(this.maintenanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar serviço sem código")
    void shouldNotCreateMaintenanceWithoutCode() {
        var command = this.commandBuilder().code(" ").build();

        assertThrows(MaintenanceCodeValueIsNullException.class, () -> this.createMaintenanceAppService.execute(command));

        verifyNoInteractions(this.maintenanceRepository);
    }

    @Test
    @DisplayName("Não deve criar serviço com preço negativo")
    void shouldNotCreateMaintenanceWithNegativePrice() {
        var command = this.commandBuilder().price(new BigDecimal("-1.00")).build();

        assertThrows(MaintenancePriceInvalidException.class, () -> this.createMaintenanceAppService.execute(command));

        verifyNoInteractions(this.maintenanceRepository);
    }

    @Test
    @DisplayName("Não deve criar serviço com duração estimada zerada")
    void shouldNotCreateMaintenanceWithZeroEstimatedDuration() {
        var command = this.commandBuilder().estimatedDurationMinutes(0).build();

        assertThrows(MaintenanceEstimatedDurationInvalidException.class, () -> this.createMaintenanceAppService.execute(command));

        verifyNoInteractions(this.maintenanceRepository);
    }

    private CreateMaintenanceCommand.CreateMaintenanceCommandBuilder commandBuilder() {
        return CreateMaintenanceCommand.builder()
                .code(UNSANITIZED_CODE)
                .description(DESCRIPTION)
                .price(PRICE)
                .estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES);
    }
}
