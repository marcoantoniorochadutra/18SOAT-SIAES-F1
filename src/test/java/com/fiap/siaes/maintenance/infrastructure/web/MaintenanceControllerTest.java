package com.fiap.siaes.maintenance.infrastructure.web;

import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.maintenance.application.dto.MaintenanceResponse;
import com.fiap.siaes.maintenance.application.usecase.ActivateMaintenanceUseCase;
import com.fiap.siaes.maintenance.application.usecase.CreateMaintenanceUseCase;
import com.fiap.siaes.maintenance.application.usecase.CreateMaintenanceUseCase.CreateMaintenanceCommand;
import com.fiap.siaes.maintenance.application.usecase.DeactivateMaintenanceUseCase;
import com.fiap.siaes.maintenance.application.usecase.GetMaintenanceByIdUseCase;
import com.fiap.siaes.maintenance.application.usecase.ListMaintenancesUseCase;
import com.fiap.siaes.maintenance.application.usecase.UpdateMaintenanceUseCase;
import com.fiap.siaes.maintenance.application.usecase.UpdateMaintenanceUseCase.UpdateMaintenanceCommand;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceAlreadyActiveException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceAlreadyInactiveException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceNotFoundException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceCodeAlreadyExistsException;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.utils.ControllerTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static com.fiap.siaes.maintenance.MaintenanceTestFactory.DESCRIPTION;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.ESTIMATED_DURATION_MINUTES;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.CODE;
import static com.fiap.siaes.maintenance.MaintenanceTestFactory.PRICE;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MaintenanceController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Controller Test - Maintenance")
class MaintenanceControllerTest extends ControllerTestUtils {

    @MockitoBean
    private CreateMaintenanceUseCase createMaintenanceUseCase;

    @MockitoBean
    private UpdateMaintenanceUseCase updateMaintenanceUseCase;

    @MockitoBean
    private GetMaintenanceByIdUseCase getMaintenanceByIdUseCase;

    @MockitoBean
    private ListMaintenancesUseCase listMaintenancesUseCase;

    @MockitoBean
    private DeactivateMaintenanceUseCase deactivateMaintenanceUseCase;

    @MockitoBean
    private ActivateMaintenanceUseCase activateMaintenanceUseCase;

    @Test
    @DisplayName("[201] Deve criar serviço com sucesso")
    void shouldCreateMaintenance() throws Exception {
        var command = CreateMaintenanceCommand.of(CODE, DESCRIPTION, PRICE, ESTIMATED_DURATION_MINUTES);
        var maintenanceId = MaintenanceId.generate();

        when(this.createMaintenanceUseCase.execute(command)).thenReturn(maintenanceId);

        super.executePostAuthenticated(MaintenanceController.ENDPOINT, command)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(maintenanceId.toString())));

        verify(this.createMaintenanceUseCase).execute(command);
    }

    @Test
    @DisplayName("[400] Não deve criar serviço com código em branco")
    void shouldNotCreateMaintenanceWhenCodeIsBlank() throws Exception {
        var command = CreateMaintenanceCommand.of(" ", DESCRIPTION, PRICE, ESTIMATED_DURATION_MINUTES);

        super.executePost(MaintenanceController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("MaintenanceCommand.code.notBlank"));

        verifyNoInteractions(this.createMaintenanceUseCase);
    }

    @Test
    @DisplayName("[400] Não deve criar serviço com código acima do tamanho máximo")
    void shouldNotCreateMaintenanceWhenCodeExceedsMaxSize() throws Exception {
        var command = CreateMaintenanceCommand.of("S".repeat(51), DESCRIPTION, PRICE, ESTIMATED_DURATION_MINUTES);

        super.executePost(MaintenanceController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("code"))
                .andExpect(jsonPath("$.fields[0].code").value("Size"));
    }

    @Test
    @DisplayName("[400] Não deve criar serviço com descrição em branco")
    void shouldNotCreateMaintenanceWhenDescriptionIsBlank() throws Exception {
        var command = CreateMaintenanceCommand.of(CODE, " ", PRICE, ESTIMATED_DURATION_MINUTES);

        super.executePost(MaintenanceController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("MaintenanceCommand.description.notBlank"));
    }

    @Test
    @DisplayName("[400] Não deve criar serviço sem preço")
    void shouldNotCreateMaintenanceWhenPriceIsNull() throws Exception {
        var command = CreateMaintenanceCommand.of(CODE, DESCRIPTION, null, ESTIMATED_DURATION_MINUTES);

        super.executePost(MaintenanceController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("MaintenanceCommand.price.notNull"));
    }

    @Test
    @DisplayName("[400] Não deve criar serviço com preço negativo")
    void shouldNotCreateMaintenanceWhenPriceIsNegative() throws Exception {
        var command = CreateMaintenanceCommand.of(
                CODE,
                DESCRIPTION,
                new BigDecimal("-1.00"),
                ESTIMATED_DURATION_MINUTES);

        super.executePost(MaintenanceController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("MaintenanceCommand.price.positiveOrZero"));
    }

    @Test
    @DisplayName("[400] Não deve criar serviço com duração estimada zerada")
    void shouldNotCreateMaintenanceWhenEstimatedDurationIsZero() throws Exception {
        var command = CreateMaintenanceCommand.of(CODE, DESCRIPTION, PRICE, 0);

        super.executePost(MaintenanceController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("MaintenanceCommand.estimatedDuration.positive"));
    }

    @Test
    @DisplayName("[409] Não deve criar serviço com código já cadastrado")
    void shouldNotCreateMaintenanceWhenCodeAlreadyExists() throws Exception {
        var command = CreateMaintenanceCommand.of(CODE, DESCRIPTION, PRICE, ESTIMATED_DURATION_MINUTES);

        when(this.createMaintenanceUseCase.execute(command))
                .thenThrow(new MaintenanceCodeAlreadyExistsException(CODE));

        super.executePostAuthenticated(MaintenanceController.ENDPOINT, command)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Código já cadastrado"));
    }

    @Test
    @DisplayName("[200] Deve atualizar serviço com sucesso")
    void shouldUpdateMaintenance() throws Exception {
        var id = MaintenanceId.generate();
        var command = this.updateCommandBuilder().build();

        when(this.updateMaintenanceUseCase.execute(command.with(id))).thenReturn(id);

        super.executePutAuthenticated(MaintenanceController.ENDPOINT + "/" + id, command)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(id.toString()))
                .andExpect(header().string("Location", containsString(id.toString())));

        verify(this.updateMaintenanceUseCase).execute(command.with(id));
    }

    @Test
    @DisplayName("[400] Não deve atualizar serviço com código em branco")
    void shouldNotUpdateMaintenanceWhenCodeIsBlank() throws Exception {
        var id = MaintenanceId.generate();
        var command = this.updateCommandBuilder().code(" ").build();

        super.executePut(MaintenanceController.ENDPOINT + "/" + id, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("MaintenanceCommand.code.notBlank"));

        verifyNoInteractions(this.updateMaintenanceUseCase);
    }

    @Test
    @DisplayName("[404] Não deve atualizar serviço inexistente")
    void shouldNotUpdateNonExistentMaintenance() throws Exception {
        var id = MaintenanceId.generate();
        var command = this.updateCommandBuilder().build();

        when(this.updateMaintenanceUseCase.execute(command.with(id)))
                .thenThrow(new MaintenanceNotFoundException(id.toString()));

        super.executePutAuthenticated(MaintenanceController.ENDPOINT + "/" + id, command)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Serviço não encontrado"));
    }

    @Test
    @DisplayName("[409] Não deve atualizar serviço com código já cadastrado")
    void shouldNotUpdateMaintenanceWhenCodeAlreadyExists() throws Exception {
        var id = MaintenanceId.generate();
        var command = this.updateCommandBuilder().build();

        when(this.updateMaintenanceUseCase.execute(command.with(id)))
                .thenThrow(new MaintenanceCodeAlreadyExistsException(CODE));

        super.executePutAuthenticated(MaintenanceController.ENDPOINT + "/" + id, command)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Código já cadastrado"));
    }

    @Test
    @DisplayName("[200] Deve buscar serviço pelo id com sucesso")
    void shouldGetMaintenanceById() throws Exception {
        var id = MaintenanceId.generate();
        var response = MaintenanceResponse.builder()
                .id(id)
                .code(CODE)
                .description(DESCRIPTION)
                .price(PRICE)
                .estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES)
                .active(true)
                .build();

        when(this.getMaintenanceByIdUseCase.execute(id)).thenReturn(response);

        super.executeGetAuthenticated(MaintenanceController.ENDPOINT + "/" + id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.code").value(CODE))
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.price").value(PRICE.doubleValue()))
                .andExpect(jsonPath("$.estimatedDurationMinutes").value(ESTIMATED_DURATION_MINUTES))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("[404] Não deve buscar serviço inexistente")
    void shouldNotGetNonExistentMaintenance() throws Exception {
        var id = MaintenanceId.generate();

        when(this.getMaintenanceByIdUseCase.execute(id)).thenThrow(new MaintenanceNotFoundException(id.toString()));

        super.executeGetAuthenticated(MaintenanceController.ENDPOINT + "/" + id)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Serviço não encontrado"));
    }

    @Test
    @DisplayName("[200] Deve listar serviços com sucesso")
    void shouldListMaintenances() throws Exception {
        var response = MaintenanceResponse.builder()
                .id(MaintenanceId.generate())
                .code(CODE)
                .description(DESCRIPTION)
                .price(PRICE)
                .estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES)
                .active(true)
                .build();
        var page = new PageResponse<>(List.of(response), false, 0, 20);

        when(this.listMaintenancesUseCase.execute(any())).thenReturn(page);

        this.mockMvc.perform(get(MaintenanceController.ENDPOINT).param("code", CODE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value(CODE))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("[400] Não deve listar serviços com tamanho de página inválido")
    void shouldNotListMaintenancesWhenPageSizeExceedsLimit() throws Exception {
        this.mockMvc.perform(get(MaintenanceController.ENDPOINT).param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("size"))
                .andExpect(jsonPath("$.fields[0].code").value("Max"));

        verifyNoInteractions(this.listMaintenancesUseCase);
    }

    @Test
    @DisplayName("[204] Deve inativar serviço com sucesso")
    void shouldDeactivateMaintenance() throws Exception {
        var id = MaintenanceId.generate();

        super.executePatch(MaintenanceController.ENDPOINT + "/" + id + "/deactivate")
                .andExpect(status().isNoContent());

        verify(this.deactivateMaintenanceUseCase).execute(id);
    }

    @Test
    @DisplayName("[404] Não deve inativar serviço inexistente")
    void shouldNotDeactivateNonExistentMaintenance() throws Exception {
        var id = MaintenanceId.generate();

        doThrow(new MaintenanceNotFoundException(id.toString())).when(this.deactivateMaintenanceUseCase).execute(id);

        super.executePatch(MaintenanceController.ENDPOINT + "/" + id + "/deactivate")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Serviço não encontrado"));
    }

    @Test
    @DisplayName("[409] Não deve inativar serviço já inativo")
    void shouldNotDeactivateAlreadyInactiveMaintenance() throws Exception {
        var id = MaintenanceId.generate();

        doThrow(new MaintenanceAlreadyInactiveException(id.toString())).when(this.deactivateMaintenanceUseCase).execute(id);

        super.executePatch(MaintenanceController.ENDPOINT + "/" + id + "/deactivate")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Serviço já inativo"));
    }

    @Test
    @DisplayName("[204] Deve ativar serviço com sucesso")
    void shouldActivateMaintenance() throws Exception {
        var id = MaintenanceId.generate();

        super.executePatch(MaintenanceController.ENDPOINT + "/" + id + "/activate")
                .andExpect(status().isNoContent());

        verify(this.activateMaintenanceUseCase).execute(id);
    }

    @Test
    @DisplayName("[404] Não deve ativar serviço inexistente")
    void shouldNotActivateNonExistentMaintenance() throws Exception {
        var id = MaintenanceId.generate();

        doThrow(new MaintenanceNotFoundException(id.toString())).when(this.activateMaintenanceUseCase).execute(id);

        super.executePatch(MaintenanceController.ENDPOINT + "/" + id + "/activate")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Serviço não encontrado"));
    }

    @Test
    @DisplayName("[409] Não deve ativar serviço já ativo")
    void shouldNotActivateAlreadyActiveMaintenance() throws Exception {
        var id = MaintenanceId.generate();

        doThrow(new MaintenanceAlreadyActiveException(id.toString())).when(this.activateMaintenanceUseCase).execute(id);

        super.executePatch(MaintenanceController.ENDPOINT + "/" + id + "/activate")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Serviço já ativo"));
    }

    private UpdateMaintenanceCommand.UpdateMaintenanceCommandBuilder updateCommandBuilder() {
        return UpdateMaintenanceCommand.builder()
                .code(CODE)
                .description(DESCRIPTION)
                .price(PRICE)
                .estimatedDurationMinutes(ESTIMATED_DURATION_MINUTES);
    }
}
