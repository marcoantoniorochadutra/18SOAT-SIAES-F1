package com.fiap.siaes.vehicle.infrastructure.web;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.sk.pagination.PageResponse;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.utils.ControllerTestUtils;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse.VehicleOwnerResponse;
import com.fiap.siaes.vehicle.application.usecase.CreateVehicleUseCase;
import com.fiap.siaes.vehicle.application.usecase.CreateVehicleUseCase.CreateVehicleCommand;
import com.fiap.siaes.vehicle.application.usecase.DeleteVehicleUseCase;
import com.fiap.siaes.vehicle.application.usecase.DeleteVehicleUseCase.DeleteVehicleCommand;
import com.fiap.siaes.vehicle.application.usecase.GetVehicleByIdUseCase;
import com.fiap.siaes.vehicle.application.usecase.GetVehicleByIdUseCase.GetVehicleByIdCommand;
import com.fiap.siaes.vehicle.application.usecase.ListVehiclesUseCase;
import com.fiap.siaes.vehicle.application.usecase.UpdateVehicleUseCase;
import com.fiap.siaes.vehicle.application.usecase.UpdateVehicleUseCase.UpdateVehicleCommand;
import com.fiap.siaes.vehicle.domain.exception.InvalidVehicleYearException;
import com.fiap.siaes.vehicle.domain.exception.VehicleNotFoundException;
import com.fiap.siaes.vehicle.domain.exception.VehiclePlateAlreadyExistsException;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.fiap.siaes.vehicle.VehicleTestFactory.PLATE;
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

@WebMvcTest(VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Controller Test - Vehicle")
class VehicleControllerTest extends ControllerTestUtils {

    @MockitoBean
    private CreateVehicleUseCase createVehicleUseCase;

    @MockitoBean
    private UpdateVehicleUseCase updateVehicleUseCase;

    @MockitoBean
    private DeleteVehicleUseCase deleteVehicleUseCase;

    @MockitoBean
    private GetVehicleByIdUseCase getVehicleByIdUseCase;

    @MockitoBean
    private ListVehiclesUseCase listVehiclesUseCase;

    @Test
    @DisplayName("[201] Deve criar veículo com sucesso")
    void shouldCreateVehicle() throws Exception {
        var command = this.createCommandBuilder().build();
        var vehicleId = VehicleId.generate();

        when(this.createVehicleUseCase.execute(command.with(AUTHENTICATED_USER_ADMIN))).thenReturn(vehicleId);

        super.executePostAuthenticated(VehicleController.ENDPOINT, command)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(vehicleId.toString())));

        verify(this.createVehicleUseCase).execute(command.with(AUTHENTICATED_USER_ADMIN));
    }

    @Test
    @DisplayName("[201] Deve repassar o usuário CUSTOMER autenticado ao criar veículo")
    void shouldCreateVehicleWithAuthenticatedCustomer() throws Exception {
        var command = this.createCommandBuilder().build();
        var authenticatedCustomer = UserTestFactory.authenticatedUserCustomer(CustomerId.generate());

        when(this.createVehicleUseCase.execute(command.with(authenticatedCustomer))).thenReturn(VehicleId.generate());

        super.executePostAuthenticated(VehicleController.ENDPOINT, command, authenticatedCustomer)
                .andExpect(status().isCreated());

        verify(this.createVehicleUseCase).execute(command.with(authenticatedCustomer));
    }

    @Test
    @DisplayName("[400] Não deve criar veículo sem cliente")
    void shouldNotCreateVehicleWhenCustomerIdIsNull() throws Exception {
        var command = this.createCommandBuilder().customerId(null).build();

        super.executePost(VehicleController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("VehicleCommand.customerId.notNull"));

        verifyNoInteractions(this.createVehicleUseCase);
    }

    @Test
    @DisplayName("[400] Não deve criar veículo com placa em branco")
    void shouldNotCreateVehicleWhenPlateIsBlank() throws Exception {
        var command = this.createCommandBuilder().plate(" ").build();

        super.executePost(VehicleController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("VehicleCommand.plate.notBlank"));
    }

    @Test
    @DisplayName("[400] Não deve criar veículo com placa acima do tamanho máximo")
    void shouldNotCreateVehicleWhenPlateExceedsMaxSize() throws Exception {
        var command = this.createCommandBuilder().plate("ABC1D23456789").build();

        super.executePost(VehicleController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("plate"))
                .andExpect(jsonPath("$.fields[0].code").value("Size"));
    }

    @Test
    @DisplayName("[400] Não deve criar veículo com marca em branco")
    void shouldNotCreateVehicleWhenBrandIsBlank() throws Exception {
        var command = this.createCommandBuilder().brand(" ").build();

        super.executePost(VehicleController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("VehicleCommand.brand.notBlank"));
    }

    @Test
    @DisplayName("[400] Não deve criar veículo com modelo em branco")
    void shouldNotCreateVehicleWhenModelIsBlank() throws Exception {
        var command = this.createCommandBuilder().model(" ").build();

        super.executePost(VehicleController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("VehicleCommand.model.notBlank"));
    }

    @Test
    @DisplayName("[400] Não deve criar veículo com ano anterior a 1900")
    void shouldNotCreateVehicleWhenYearIsBeforeMinimum() throws Exception {
        var command = this.createCommandBuilder().year(1899).build();

        super.executePost(VehicleController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("year"))
                .andExpect(jsonPath("$.fields[0].code").value("Min"));
    }

    @Test
    @DisplayName("[400] Não deve criar veículo com ano posterior ao próximo ano")
    void shouldNotCreateVehicleWhenYearIsInvalid() throws Exception {
        var command = this.createCommandBuilder().year(3000).build();

        when(this.createVehicleUseCase.execute(command.with(AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new InvalidVehicleYearException(3000));

        super.executePostAuthenticated(VehicleController.ENDPOINT, command)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Ano do veículo inválido"));
    }

    @Test
    @DisplayName("[409] Não deve criar veículo com placa já cadastrada")
    void shouldNotCreateVehicleWhenPlateAlreadyExists() throws Exception {
        var command = this.createCommandBuilder().build();

        when(this.createVehicleUseCase.execute(command.with(AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new VehiclePlateAlreadyExistsException(PLATE));

        super.executePostAuthenticated(VehicleController.ENDPOINT, command)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Placa já cadastrada"));
    }

    @Test
    @DisplayName("[200] Deve atualizar veículo com sucesso")
    void shouldUpdateVehicle() throws Exception {
        var id = VehicleId.generate();
        var command = this.updateCommandBuilder().build();

        when(this.updateVehicleUseCase.execute(command.with(id, AUTHENTICATED_USER_ADMIN))).thenReturn(id);

        super.executePutAuthenticated(VehicleController.ENDPOINT + "/" + id, command)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(id.toString()))
                .andExpect(header().string("Location", containsString(id.toString())));

        verify(this.updateVehicleUseCase).execute(command.with(id, AUTHENTICATED_USER_ADMIN));
    }

    @Test
    @DisplayName("[400] Não deve atualizar veículo com placa em branco")
    void shouldNotUpdateVehicleWhenPlateIsBlank() throws Exception {
        var id = VehicleId.generate();
        var command = this.updateCommandBuilder().plate(" ").build();

        super.executePut(VehicleController.ENDPOINT + "/" + id, command)
                .andExpect(status().isBadRequest())
                .andExpect(super.matchValidationMessage("VehicleCommand.plate.notBlank"));

        verifyNoInteractions(this.updateVehicleUseCase);
    }

    @Test
    @DisplayName("[404] Não deve atualizar veículo inexistente")
    void shouldNotUpdateNonExistentVehicle() throws Exception {
        var id = VehicleId.generate();
        var command = this.updateCommandBuilder().build();

        when(this.updateVehicleUseCase.execute(command.with(id, AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new VehicleNotFoundException(id.toString()));

        super.executePutAuthenticated(VehicleController.ENDPOINT + "/" + id, command)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Veículo não encontrado"));
    }

    @Test
    @DisplayName("[409] Não deve atualizar veículo com placa já cadastrada")
    void shouldNotUpdateVehicleWhenPlateAlreadyExists() throws Exception {
        var id = VehicleId.generate();
        var command = this.updateCommandBuilder().build();

        when(this.updateVehicleUseCase.execute(command.with(id, AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new VehiclePlateAlreadyExistsException(PLATE));

        super.executePutAuthenticated(VehicleController.ENDPOINT + "/" + id, command)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Placa já cadastrada"));
    }

    @Test
    @DisplayName("[200] Deve buscar veículo pelo id com sucesso")
    void shouldGetVehicleById() throws Exception {
        var id = VehicleId.generate();
        var customerId = CustomerId.generate();
        var response = new VehicleResponse(id, PLATE, "Uno", 2020, new VehicleOwnerResponse(customerId, "Maria Silva"));

        when(this.getVehicleByIdUseCase.execute(GetVehicleByIdCommand.from(id, AUTHENTICATED_USER_ADMIN))).thenReturn(response);

        super.executeGetAuthenticated(VehicleController.ENDPOINT + "/" + id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.plate").value(PLATE))
                .andExpect(jsonPath("$.model").value("Uno"))
                .andExpect(jsonPath("$.year").value(2020))
                .andExpect(jsonPath("$.owner.id").value(customerId.toString()))
                .andExpect(jsonPath("$.owner.name").value("Maria Silva"));
    }

    @Test
    @DisplayName("[404] Não deve buscar veículo inexistente")
    void shouldNotGetNonExistentVehicle() throws Exception {
        var id = VehicleId.generate();

        when(this.getVehicleByIdUseCase.execute(GetVehicleByIdCommand.from(id, AUTHENTICATED_USER_ADMIN)))
                .thenThrow(new VehicleNotFoundException(id.toString()));

        super.executeGetAuthenticated(VehicleController.ENDPOINT + "/" + id)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Veículo não encontrado"));
    }

    @Test
    @DisplayName("[200] Deve listar veículos com sucesso")
    void shouldListVehicles() throws Exception {
        var response = new VehicleResponse(
                VehicleId.generate(), PLATE, "Uno", 2020, new VehicleOwnerResponse(CustomerId.generate(), "Maria Silva"));
        var page = new PageResponse<>(List.of(response), false, 0, 20);

        when(this.listVehiclesUseCase.execute(any())).thenReturn(page);

        this.mockMvc.perform(get(VehicleController.ENDPOINT).param("plate", PLATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].plate").value(PLATE))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("[400] Não deve listar veículos com tamanho de página inválido")
    void shouldNotListVehiclesWhenPageSizeExceedsLimit() throws Exception {
        this.mockMvc.perform(get(VehicleController.ENDPOINT).param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("size"))
                .andExpect(jsonPath("$.fields[0].code").value("Max"));

        verifyNoInteractions(this.listVehiclesUseCase);
    }

    @Test
    @DisplayName("[204] Deve remover veículo com sucesso")
    void shouldDeleteVehicle() throws Exception {
        var id = VehicleId.generate();

        super.authenticateAs(AUTHENTICATED_USER_ADMIN);
        super.executeDelete(VehicleController.ENDPOINT + "/" + id)
                .andExpect(status().isNoContent());

        verify(this.deleteVehicleUseCase).execute(DeleteVehicleCommand.from(id, AUTHENTICATED_USER_ADMIN));
    }

    @Test
    @DisplayName("[404] Não deve remover veículo inexistente")
    void shouldNotDeleteNonExistentVehicle() throws Exception {
        var id = VehicleId.generate();
        var command = DeleteVehicleCommand.from(id, AUTHENTICATED_USER_ADMIN);

        doThrow(new VehicleNotFoundException(id.toString())).when(this.deleteVehicleUseCase).execute(command);

        super.authenticateAs(AUTHENTICATED_USER_ADMIN);
        super.executeDelete(VehicleController.ENDPOINT + "/" + id)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Veículo não encontrado"));

        verify(this.deleteVehicleUseCase).execute(command);
    }

    private CreateVehicleCommand.CreateVehicleCommandBuilder createCommandBuilder() {
        return CreateVehicleCommand.builder()
                .customerId(CustomerId.generate())
                .plate(PLATE)
                .brand("Fiat")
                .model("Uno")
                .year(2020);
    }

    private UpdateVehicleCommand.UpdateVehicleCommandBuilder updateCommandBuilder() {
        return UpdateVehicleCommand.builder()
                .plate(PLATE)
                .brand("Fiat")
                .model("Uno")
                .year(2021);
    }
}
