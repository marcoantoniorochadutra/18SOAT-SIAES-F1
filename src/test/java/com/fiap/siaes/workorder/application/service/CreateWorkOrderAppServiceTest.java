package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.customer.domain.exception.CustomerNotFoundException;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.vehicle.VehicleTestFactory;
import com.fiap.siaes.vehicle.domain.exception.VehicleNotFoundException;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import com.fiap.siaes.workorder.application.usecase.CreateWorkOrderUseCase.CreateWorkOrderCommand;
import com.fiap.siaes.workorder.domain.exception.WorkOrderVehicleCustomerMismatchException;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Create Work Order")
class CreateWorkOrderAppServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private CreateWorkOrderAppService createWorkOrderAppService;

    @Test
    @DisplayName("Deve criar uma ordem de serviço recebida")
    void shouldCreateWorkOrderSuccessfully() {
        Vehicle vehicle = VehicleTestFactory.createVehicle();
        var command = CreateWorkOrderCommand.of(vehicle.getCustomerId(), vehicle.getId());
        var captor = ArgumentCaptor.forClass(WorkOrder.class);

        when(this.vehicleRepository.existsByIdAndCustomerId(vehicle.getId(), vehicle.getCustomerId())).thenReturn(true);

        WorkOrderId workOrderId = this.createWorkOrderAppService.execute(command);

        verify(this.workOrderRepository).save(captor.capture());
        WorkOrder saved = captor.getValue();
        assertEquals(saved.getId(), workOrderId);
        assertEquals(vehicle.getCustomerId(), saved.getCustomerId());
        assertEquals(vehicle.getId(), saved.getVehicleId());
        assertEquals(WorkOrderStatus.RECEIVED, saved.getLastStatus());
    }

    @Test
    @DisplayName("Não deve criar ordem de serviço quando o cliente não existir")
    void shouldNotCreateWorkOrderWhenCustomerDoesNotExist() {
        Vehicle vehicle = VehicleTestFactory.createVehicle();
        var command = CreateWorkOrderCommand.of(vehicle.getCustomerId(), vehicle.getId());

        doThrow(new CustomerNotFoundException(command.customerId().toString()))
                .when(this.customerRepository).existsByIdOrThrowNotFound(command.customerId());

        assertThrows(CustomerNotFoundException.class, () -> this.createWorkOrderAppService.execute(command));

        verify(this.workOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar ordem de serviço quando o veículo não existir")
    void shouldNotCreateWorkOrderWhenVehicleDoesNotExist() {
        Vehicle vehicle = VehicleTestFactory.createVehicle();
        var command = CreateWorkOrderCommand.of(vehicle.getCustomerId(), vehicle.getId());

        doThrow(new VehicleNotFoundException(command.vehicleId().toString()))
                .when(this.vehicleRepository).existsByIdOrThrowNotFound(command.vehicleId());

        assertThrows(VehicleNotFoundException.class, () -> this.createWorkOrderAppService.execute(command));

        verify(this.workOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve criar ordem de serviço quando o veículo não pertencer ao cliente")
    void shouldNotCreateWorkOrderWhenVehicleBelongsToAnotherCustomer() {
        Vehicle vehicle = VehicleTestFactory.createVehicle();
        var otherCustomerId = CustomerId.generate();
        var command = CreateWorkOrderCommand.of(otherCustomerId, vehicle.getId());

        when(this.vehicleRepository.existsByIdAndCustomerId(vehicle.getId(), otherCustomerId)).thenReturn(false);

        var exception = assertThrows(WorkOrderVehicleCustomerMismatchException.class,
                () -> this.createWorkOrderAppService.execute(command));

        assertExceptionParameters(exception, vehicle.getId().toString(), otherCustomerId.toString());
        verify(this.workOrderRepository, never()).save(any());
    }
}
