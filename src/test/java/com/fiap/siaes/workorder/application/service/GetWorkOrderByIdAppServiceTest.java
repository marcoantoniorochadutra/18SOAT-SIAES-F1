package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.customer.domain.model.Customer;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.vehicle.domain.model.Vehicle;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import com.fiap.siaes.workorder.application.dto.WorkOrderResponse;
import com.fiap.siaes.workorder.application.usecase.GetWorkOrderByIdUseCase.GetWorkOrderByIdCommand;
import com.fiap.siaes.workorder.domain.exception.WorkOrderNotFoundException;
import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.fiap.siaes.customer.CustomerTestFactory.createCustomer;
import static com.fiap.siaes.vehicle.VehicleTestFactory.PLATE;
import static com.fiap.siaes.vehicle.VehicleTestFactory.createVehicle;
import static com.fiap.siaes.workorder.WorkOrderTestFactory.createWorkOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Get Work Order By Id")
class GetWorkOrderByIdAppServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private GetWorkOrderByIdAppService getWorkOrderByIdAppService;

    @Test
    @DisplayName("Deve retornar a ordem de serviço com cliente, veículo e histórico de status")
    void shouldReturnWorkOrder() {
        WorkOrder workOrder = createWorkOrder();
        Customer customer = createCustomer();
        Vehicle vehicle = createVehicle();

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);
        when(this.customerRepository.findByIdOrThrowNotFound(workOrder.getCustomerId())).thenReturn(customer);
        when(this.vehicleRepository.findByIdOrThrowNotFound(workOrder.getVehicleId())).thenReturn(vehicle);

        WorkOrderResponse response = this.getWorkOrderByIdAppService.execute(
                GetWorkOrderByIdCommand.of(workOrder.getId(), UserTestFactory.authenticatedUserManager()));

        assertEquals(workOrder.getId(), response.id());
        assertEquals(customer.getId(), response.customer().id());
        assertEquals(customer.getName(), response.customer().name());
        assertEquals(vehicle.getId(), response.vehicle().id());
        assertEquals(PLATE, response.vehicle().plate());
        assertEquals(vehicle.getModel(), response.vehicle().model());
        assertEquals(WorkOrderStatus.RECEIVED, response.status());
        assertEquals(1, response.statusHistory().size());
    }

    @Test
    @DisplayName("Deve permitir que o cliente consulte a própria ordem de serviço")
    void shouldReturnWorkOrderToItsCustomer() {
        var customerId = CustomerId.generate();
        WorkOrder workOrder = createWorkOrder(customerId, WorkOrderStatus.RECEIVED);

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);
        when(this.customerRepository.findByIdOrThrowNotFound(customerId)).thenReturn(createCustomer());
        when(this.vehicleRepository.findByIdOrThrowNotFound(workOrder.getVehicleId())).thenReturn(createVehicle(customerId));

        WorkOrderResponse response = this.getWorkOrderByIdAppService.execute(
                GetWorkOrderByIdCommand.of(workOrder.getId(), UserTestFactory.authenticatedUserCustomer(customerId)));

        assertEquals(workOrder.getId(), response.id());
    }

    @Test
    @DisplayName("Não deve retornar ordem de serviço de outro cliente")
    void shouldNotReturnWorkOrderOfAnotherCustomer() {
        WorkOrder workOrder = createWorkOrder();
        var command = GetWorkOrderByIdCommand.of(workOrder.getId(), UserTestFactory.authenticatedUserCustomer(CustomerId.generate()));

        when(this.workOrderRepository.findByIdOrThrowNotFound(workOrder.getId())).thenReturn(workOrder);

        assertThrows(WorkOrderNotFoundException.class, () -> this.getWorkOrderByIdAppService.execute(command));
    }
}
