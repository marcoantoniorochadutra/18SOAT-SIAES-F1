package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.vehicle.application.usecase.DeleteVehicleUseCase.DeleteVehicleCommand;
import com.fiap.siaes.vehicle.domain.exception.VehicleNotFoundException;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Delete Vehicle")
class DeleteVehicleAppServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private DeleteVehicleAppService deleteVehicleAppService;

    @Test
    @DisplayName("Deve remover um veículo com sucesso")
    void shouldDeleteVehicleSuccessfully() {
        var vehicle = createVehicle();

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);

        this.deleteVehicleAppService.execute(DeleteVehicleCommand.from(vehicle.getId(), UserTestFactory.authenticatedUserManager()));

        verify(this.vehicleRepository).deleteById(vehicle.getId());
    }

    @Test
    @DisplayName("Deve remover o próprio veículo quando o usuário autenticado é CUSTOMER")
    void shouldDeleteOwnVehicleWhenAuthenticatedUserIsCustomer() {
        var customerId = CustomerId.generate();
        var vehicle = createVehicle(customerId);

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);

        this.deleteVehicleAppService.execute(DeleteVehicleCommand.from(vehicle.getId(), UserTestFactory.authenticatedUserCustomer(customerId)));

        verify(this.vehicleRepository).deleteById(vehicle.getId());
    }

    @Test
    @DisplayName("Não deve remover veículo de outro cliente quando o usuário autenticado é CUSTOMER")
    void shouldNotDeleteVehicleOfAnotherCustomerWhenAuthenticatedUserIsCustomer() {
        var vehicle = createVehicle();

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);

        var command = DeleteVehicleCommand.from(vehicle.getId(), UserTestFactory.authenticatedUserCustomer(CustomerId.generate()));
        var exception = assertThrows(VehicleNotFoundException.class, () -> this.deleteVehicleAppService.execute(command));

        assertExceptionParameters(exception, vehicle.getId().toString());
        verify(this.vehicleRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Não deve remover veículo inexistente")
    void shouldNotDeleteVehicleWhenNotFound() {
        var id = VehicleId.generate();

        when(this.vehicleRepository.findByIdOrThrowNotFound(id)).thenThrow(new VehicleNotFoundException(id.toString()));

        var command = DeleteVehicleCommand.from(id, UserTestFactory.authenticatedUserManager());
        var exception = assertThrows(VehicleNotFoundException.class, () -> this.deleteVehicleAppService.execute(command));

        assertExceptionParameters(exception, id.toString());
        verify(this.vehicleRepository, never()).deleteById(any());
    }
}
