package com.fiap.siaes.vehicle.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Delete Customer Vehicles")
class DeleteCustomerVehiclesAppServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private DeleteCustomerVehiclesAppService deleteCustomerVehiclesAppService;

    @Test
    @DisplayName("Deve remover todos os veículos do cliente")
    void shouldDeleteAllCustomerVehicles() {
        var customerId = CustomerId.generate();

        this.deleteCustomerVehiclesAppService.execute(customerId);

        verify(this.vehicleRepository).deleteAllByCustomerId(customerId);
    }
}
