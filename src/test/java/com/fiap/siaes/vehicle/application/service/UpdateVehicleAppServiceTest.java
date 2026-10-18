package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.vehicle.application.usecase.UpdateVehicleUseCase.UpdateVehicleCommand;
import com.fiap.siaes.vehicle.domain.exception.VehicleNotFoundException;
import com.fiap.siaes.vehicle.domain.exception.VehiclePlateAlreadyExistsException;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.model.vo.Plate;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Update Vehicle")
class UpdateVehicleAppServiceTest {

    private static final String NEW_PLATE = "XYZ9K87";

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private UpdateVehicleAppService updateVehicleAppService;

    @Test
    @DisplayName("Deve atualizar um veículo com sucesso")
    void shouldUpdateVehicleSuccessfully() {
        var vehicle = createVehicle();
        var command = this.command(vehicle.getId(), UserTestFactory.authenticatedUserManager());

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);

        var id = this.updateVehicleAppService.execute(command);

        Vehicle savedVehicle = captureSave(this.vehicleRepository, Vehicle.class);

        assertEquals(vehicle.getId(), id);
        assertEquals(NEW_PLATE, savedVehicle.getPlate().getValue());
        assertEquals("Volkswagen", savedVehicle.getBrand());
        assertEquals("Gol", savedVehicle.getModel());
        assertEquals(2021, savedVehicle.getYear());
    }

    @Test
    @DisplayName("Deve atualizar o próprio veículo quando o usuário autenticado é CUSTOMER")
    void shouldUpdateOwnVehicleWhenAuthenticatedUserIsCustomer() {
        var customerId = CustomerId.generate();
        var vehicle = createVehicle(customerId);
        var command = this.command(vehicle.getId(), UserTestFactory.authenticatedUserCustomer(customerId));

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);

        this.updateVehicleAppService.execute(command);

        assertEquals("Gol", vehicle.getModel());
        verify(this.vehicleRepository).save(vehicle);
    }

    @Test
    @DisplayName("Não deve atualizar veículo de outro cliente quando o usuário autenticado é CUSTOMER")
    void shouldNotUpdateVehicleOfAnotherCustomerWhenAuthenticatedUserIsCustomer() {
        var vehicle = createVehicle();
        var command = this.command(vehicle.getId(), UserTestFactory.authenticatedUserCustomer(CustomerId.generate()));

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);

        var exception = assertThrows(VehicleNotFoundException.class,
                () -> this.updateVehicleAppService.execute(command));

        assertExceptionParameters(exception, vehicle.getId().toString());
        assertEquals("Uno", vehicle.getModel());
        verify(this.vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve atualizar veículo quando o usuário CUSTOMER não possui cliente vinculado")
    void shouldNotUpdateVehicleWhenCustomerUserHasNoLinkedCustomer() {
        var vehicle = createVehicle();
        var command = this.command(vehicle.getId(), UserTestFactory.authenticatedUserCustomer());

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);

        assertThrows(VehicleNotFoundException.class, () -> this.updateVehicleAppService.execute(command));

        verify(this.vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o veículo a ser atualizado não existir")
    void shouldThrowWhenVehicleToUpdateDoesNotExist() {
        var id = VehicleId.generate();
        var command = this.command(id, UserTestFactory.authenticatedUserManager());

        when(this.vehicleRepository.findByIdOrThrowNotFound(id))
                .thenThrow(new VehicleNotFoundException(id.toString()));

        var exception = assertThrows(VehicleNotFoundException.class,
                () -> this.updateVehicleAppService.execute(command));

        assertExceptionParameters(exception, id.toString());
        verify(this.vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve atualizar quando a placa já pertencer a outro veículo")
    void shouldNotUpdateWhenPlateAlreadyBelongsToAnotherVehicle() {
        var vehicle = createVehicle();
        var command = this.command(vehicle.getId(), UserTestFactory.authenticatedUserManager());

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);
        when(this.vehicleRepository.existsByPlateAndIdNot(Plate.recreate(NEW_PLATE), vehicle.getId())).thenReturn(true);

        var exception = assertThrows(VehiclePlateAlreadyExistsException.class,
                () -> this.updateVehicleAppService.execute(command));

        assertExceptionParameters(exception, NEW_PLATE);
        verify(this.vehicleRepository, never()).save(any());
    }

    private UpdateVehicleCommand command(VehicleId id, AuthenticatedUser authenticatedUser) {
        return UpdateVehicleCommand.builder()
                .id(id)
                .authenticatedUser(authenticatedUser)
                .plate("xyz-9k87")
                .brand("Volkswagen")
                .model("Gol")
                .year(2021)
                .build();
    }
}
