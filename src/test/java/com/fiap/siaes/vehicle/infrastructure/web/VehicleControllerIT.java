package com.fiap.siaes.vehicle.infrastructure.web;

import com.fiap.siaes.customer.CustomerTestFactory;
import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.utils.IntegrationTestUtils;
import com.fiap.siaes.vehicle.VehicleTestFactory;
import com.fiap.siaes.vehicle.application.usecase.CreateVehicleUseCase.CreateVehicleCommand;
import com.fiap.siaes.vehicle.application.usecase.UpdateVehicleUseCase.UpdateVehicleCommand;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.utils.TestFactory.persist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration Test - Vehicle")
class VehicleControllerIT extends IntegrationTestUtils {

    private String managerToken;
    private Customer customer;

    @BeforeEach
    void setUp() {
        this.managerToken = super.loginAs(UserRole.MANAGER);
        this.customer = persist(super.customerRepository, CustomerTestFactory.createCustomer());
    }

    @Test
    @DisplayName("[201] Deve criar veículo")
    void shouldCreateVehicle() throws Exception {
        var command = CreateVehicleCommand.builder()
                .customerId(this.customer.getId())
                .plate("BRA2E19")
                .brand("Fiat")
                .model("Uno")
                .year(2020)
                .build();

        var id = super.createdId(super.executePost(VehicleController.ENDPOINT, this.managerToken, command));

        var vehicle = super.vehicleRepository.findByIdOrThrowNotFound(new VehicleId(id));
        assertEquals("BRA2E19", vehicle.getPlate().getValue());
        assertEquals(this.customer.getId(), vehicle.getCustomerId());
    }

    @Test
    @DisplayName("[200] Deve buscar veículo")
    void shouldGetVehicle() throws Exception {
        var vehicle = this.persistVehicle();

        super.executeGet(this.url(vehicle), this.managerToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.id", vehicle.getId().toString()))
                .andExpect(jsonValue("$.plate", vehicle.getPlate().getValue()))
                .andExpect(jsonValue("$.owner.id", this.customer.getId().toString()));
    }

    @Test
    @DisplayName("[200] Deve atualizar veículo")
    void shouldUpdateVehicle() throws Exception {
        var vehicle = this.persistVehicle();
        var command = UpdateVehicleCommand.builder()
                .plate(VehicleTestFactory.PLATE)
                .brand("Fiat")
                .model("Mobi")
                .year(2022)
                .build();

        super.executePut(this.url(vehicle), this.managerToken, command)
                .andExpect(status().isOk());

        var updated = super.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId());
        assertEquals("Mobi", updated.getModel());
        assertEquals(2022, updated.getYear());
    }

    @Test
    @DisplayName("[200] Deve listar veículos")
    void shouldListVehicles() throws Exception {
        var vehicle = this.persistVehicle();

        super.executeGet(VehicleController.ENDPOINT, this.managerToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.content.length()", 1))
                .andExpect(jsonValue("$.content[0].id", vehicle.getId().toString()));
    }

    @Test
    @DisplayName("[204] Deve excluir veículo")
    void shouldDeleteVehicle() throws Exception {
        var vehicle = this.persistVehicle();

        super.executeDelete(this.url(vehicle), this.managerToken)
                .andExpect(status().isNoContent());

        assertTrue(super.vehicleRepository.findById(vehicle.getId()).isEmpty());
    }

    @Test
    @DisplayName("[200] Cliente deve buscar o próprio veículo")
    void shouldGetOwnVehicleAsCustomer() throws Exception {
        var vehicle = this.persistVehicle();
        var customerToken = super.loginAs(UserRole.CUSTOMER, this.customer.getId());

        super.executeGet(this.url(vehicle), customerToken)
                .andExpect(status().isOk())
                .andExpect(jsonValue("$.id", vehicle.getId().toString()));
    }

    @Test
    @DisplayName("[403] Mecânico não deve acessar veículos")
    void shouldNotListVehiclesAsMechanic() throws Exception {
        var mechanicToken = super.loginAs(UserRole.MECHANIC);

        super.executeGet(VehicleController.ENDPOINT, mechanicToken)
                .andExpect(status().isForbidden());
    }

    private Vehicle persistVehicle() {
        return persist(super.vehicleRepository, VehicleTestFactory.createVehicle(this.customer.getId()));
    }

    private String url(Vehicle vehicle) {
        return VehicleController.ENDPOINT + "/" + vehicle.getId();
    }
}
