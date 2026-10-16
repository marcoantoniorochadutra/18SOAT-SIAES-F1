package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.vehicle.application.usecase.CreateVehicleUseCase.CreateVehicleCommand;
import com.fiap.siaes.vehicle.domain.exception.InvalidVehicleYearException;
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

import java.time.Year;

import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.utils.TestUtils.captureSave;
import static com.fiap.siaes.vehicle.VehicleTestFactory.PLATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Create Vehicle")
class CreateVehicleAppServiceTest {

    private static final String UNSANITIZED_PLATE = "abc-1d23";

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CreateVehicleAppService createVehicleAppService;

    @Test
    @DisplayName("Deve criar um veículo com sucesso")
    void shouldCreateVehicleSuccessfully() {
        var customerId = CustomerId.generate();
        var command = this.commandBuilder()
                .customerId(customerId)
                .authenticatedUser(UserTestFactory.authenticatedUserManager())
                .build();

        VehicleId vehicleId = this.createVehicleAppService.execute(command);

        Vehicle savedVehicle = captureSave(this.vehicleRepository, Vehicle.class);

        assertEquals(savedVehicle.getId(), vehicleId);
        assertEquals(customerId, savedVehicle.getCustomerId());
        assertEquals(PLATE, savedVehicle.getPlate().getValue());
        assertEquals(command.brand(), savedVehicle.getBrand());
        assertEquals(command.model(), savedVehicle.getModel());
        assertEquals(command.year(), savedVehicle.getYear());
        verify(this.customerRepository).existsByIdOrThrowNotFound(customerId);
    }

    @Test
    @DisplayName("Deve criar o veículo no próprio cliente quando o usuário autenticado é CUSTOMER")
    void shouldCreateVehicleForOwnCustomerWhenAuthenticatedUserIsCustomer() {
        var ownCustomerId = CustomerId.generate();
        var requestedCustomerId = CustomerId.generate();
        var command = this.commandBuilder()
                .customerId(requestedCustomerId)
                .authenticatedUser(UserTestFactory.authenticatedUserCustomer(ownCustomerId))
                .build();

        this.createVehicleAppService.execute(command);

        Vehicle savedVehicle = captureSave(this.vehicleRepository, Vehicle.class);

        assertEquals(ownCustomerId, savedVehicle.getCustomerId());
        verify(this.customerRepository).existsByIdOrThrowNotFound(ownCustomerId);
        verify(this.customerRepository, never()).existsByIdOrThrowNotFound(requestedCustomerId);
    }

    @Test
    @DisplayName("Não deve criar veículo quando a placa já estiver cadastrada")
    void shouldNotCreateVehicleWhenPlateAlreadyExists() {
        var command = this.commandBuilder()
                .customerId(CustomerId.generate())
                .authenticatedUser(UserTestFactory.authenticatedUserManager())
                .build();

        when(this.vehicleRepository.existsByPlate(Plate.recreate(PLATE))).thenReturn(true);

        var exception = assertThrows(VehiclePlateAlreadyExistsException.class,
                () -> this.createVehicleAppService.execute(command));

        assertExceptionParameters(exception, PLATE);
        verify(this.vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar veículo quando o cliente não existir")
    void shouldNotCreateVehicleWhenCustomerNotFound() {
        var customerId = CustomerId.generate();
        var command = this.commandBuilder()
                .customerId(customerId)
                .authenticatedUser(UserTestFactory.authenticatedUserManager())
                .build();

        doThrow(new CustomerNotFoundException(customerId.toString()))
                .when(this.customerRepository).existsByIdOrThrowNotFound(customerId);

        var exception = assertThrows(CustomerNotFoundException.class,
                () -> this.createVehicleAppService.execute(command));

        assertExceptionParameters(exception, customerId.toString());
        verifyNoInteractions(this.vehicleRepository);
    }

    @Test
    @DisplayName("Não deve criar veículo com ano inválido")
    void shouldNotCreateVehicleWithInvalidYear() {
        int invalidYear = Year.now().getValue() + 2;
        var command = this.commandBuilder()
                .customerId(CustomerId.generate())
                .authenticatedUser(UserTestFactory.authenticatedUserManager())
                .year(invalidYear)
                .build();

        var exception = assertThrows(InvalidVehicleYearException.class,
                () -> this.createVehicleAppService.execute(command));

        assertExceptionParameters(exception, invalidYear);
        verifyNoInteractions(this.vehicleRepository);
    }

    private CreateVehicleCommand.CreateVehicleCommandBuilder commandBuilder() {
        return CreateVehicleCommand.builder()
                .plate(UNSANITIZED_PLATE)
                .brand("Fiat")
                .model("Uno")
                .year(2020);
    }
}
