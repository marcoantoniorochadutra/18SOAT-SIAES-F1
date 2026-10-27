package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Delete Customer Work Orders")
class DeleteCustomerWorkOrdersAppServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private DeleteCustomerWorkOrdersAppService deleteCustomerWorkOrdersAppService;

    @Test
    @DisplayName("Deve remover todas as ordens de serviço do cliente")
    void shouldDeleteAllCustomerWorkOrders() {
        var customerId = CustomerId.generate();

        this.deleteCustomerWorkOrdersAppService.execute(customerId);

        verify(this.workOrderRepository).deleteAllByCustomerId(customerId);
    }
}
