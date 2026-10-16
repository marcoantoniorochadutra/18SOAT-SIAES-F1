package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.vehicle.application.dto.VehicleResponse;
import com.fiap.siaes.vehicle.application.usecase.GetVehicleByIdUseCase.GetVehicleByIdCommand;
import com.fiap.siaes.vehicle.domain.exception.VehicleNotFoundException;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Get Vehicle By Id")
class GetVehicleByIdAppServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private GetVehicleByIdAppService getVehicleByIdAppService;

    @Test
    @DisplayName("Deve buscar um veículo pelo id com sucesso")
    void shouldGetVehicleSuccessfully() {
        var customer = createCustomer();
        var vehicle = createVehicle(customer.getId());

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);
        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        var command = GetVehicleByIdCommand.from(vehicle.getId(), UserTestFactory.authenticatedUserManager());
        VehicleResponse response = this.getVehicleByIdAppService.execute(command);

        assertEquals(vehicle.getId(), response.id());
        assertEquals(vehicle.getPlate().getValue(), response.plate());
        assertEquals(vehicle.getModel(), response.model());
        assertEquals(vehicle.getYear(), response.year());
        assertEquals(customer.getId(), response.owner().id());
        assertEquals(customer.getName(), response.owner().name());
    }

    @Test
    @DisplayName("Deve buscar o próprio veículo quando o usuário autenticado é CUSTOMER")
    void shouldGetOwnVehicleWhenAuthenticatedUserIsCustomer() {
        var customer = createCustomer();
        var vehicle = createVehicle(customer.getId());

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);
        when(this.customerRepository.findByIdOrThrowNotFound(customer.getId())).thenReturn(customer);

        var command = GetVehicleByIdCommand.from(vehicle.getId(), UserTestFactory.authenticatedUserCustomer(customer.getId()));
        VehicleResponse response = this.getVehicleByIdAppService.execute(command);

        assertEquals(vehicle.getId(), response.id());
    }

    @Test
    @DisplayName("Não deve buscar veículo de outro cliente quando o usuário autenticado é CUSTOMER")
    void shouldNotGetVehicleOfAnotherCustomerWhenAuthenticatedUserIsCustomer() {
        var vehicle = createVehicle();

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);

        var command = GetVehicleByIdCommand.from(vehicle.getId(), UserTestFactory.authenticatedUserCustomer(CustomerId.generate()));
        var exception = assertThrows(VehicleNotFoundException.class,
                () -> this.getVehicleByIdAppService.execute(command));

        assertExceptionParameters(exception, vehicle.getId().toString());
        verify(this.customerRepository, never()).findByIdOrThrowNotFound(any());
    }

    @Test
    @DisplayName("Não deve buscar veículo quando o usuário CUSTOMER não possui cliente vinculado")
    void shouldNotGetVehicleWhenCustomerUserHasNoLinkedCustomer() {
        var vehicle = createVehicle();

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);

        var command = GetVehicleByIdCommand.from(vehicle.getId(), UserTestFactory.authenticatedUserCustomer());

        assertThrows(VehicleNotFoundException.class, () -> this.getVehicleByIdAppService.execute(command));
        verify(this.customerRepository, never()).findByIdOrThrowNotFound(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o veículo não existir")
    void shouldThrowWhenVehicleNotFound() {
        var id = VehicleId.generate();

        when(this.vehicleRepository.findByIdOrThrowNotFound(id)).thenThrow(new VehicleNotFoundException(id.toString()));

        var command = GetVehicleByIdCommand.from(id, UserTestFactory.authenticatedUserManager());
        var exception = assertThrows(VehicleNotFoundException.class,
                () -> this.getVehicleByIdAppService.execute(command));

        assertExceptionParameters(exception, id.toString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente dono do veículo não existir")
    void shouldThrowWhenVehicleOwnerNotFound() {
        var customerId = CustomerId.generate();
        var vehicle = createVehicle(customerId);

        when(this.vehicleRepository.findByIdOrThrowNotFound(vehicle.getId())).thenReturn(vehicle);
        when(this.customerRepository.findByIdOrThrowNotFound(customerId))
                .thenThrow(new CustomerNotFoundException(customerId.toString()));

        var command = GetVehicleByIdCommand.from(vehicle.getId(), UserTestFactory.authenticatedUserManager());
        var exception = assertThrows(CustomerNotFoundException.class,
                () -> this.getVehicleByIdAppService.execute(command));

        assertExceptionParameters(exception, customerId.toString());
    }
}
