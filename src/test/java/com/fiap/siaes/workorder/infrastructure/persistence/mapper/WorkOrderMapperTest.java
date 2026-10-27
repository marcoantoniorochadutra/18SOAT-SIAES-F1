package com.fiap.siaes.workorder.infrastructure.persistence.mapper;

import com.fiap.siaes.workorder.domain.model.WorkOrder;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.workorder.WorkOrderTestFactory.createWorkOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("UnitTest - Mapper - Work Order")
class WorkOrderMapperTest {

    @Test
    @DisplayName("Deve converter domínio para entidade e de volta sem perder dados")
    void shouldMapRoundTrip() {
        WorkOrder workOrder = createWorkOrder(WorkOrderStatus.IN_EXECUTION);
        workOrder.finish();

        WorkOrder mapped = WorkOrderMapper.toDomain(WorkOrderMapper.toEntity(workOrder));

        assertEquals(workOrder.getId(), mapped.getId());
        assertEquals(workOrder.getCustomerId(), mapped.getCustomerId());
        assertEquals(workOrder.getVehicleId(), mapped.getVehicleId());
        assertEquals(WorkOrderStatus.FINISHED, mapped.getLastStatus());
        assertEquals(2, mapped.getStatusHistory().size());
        assertEquals(workOrder.getCreatedAt(), mapped.getCreatedAt());
        assertEquals(workOrder.getFinishedAt(), mapped.getFinishedAt());
        assertEquals(workOrder.getVersion(), mapped.getVersion());
    }

    @Test
    @DisplayName("Deve retornar nulo ao converter valores nulos")
    void shouldReturnNullForNullInput() {
        assertNull(WorkOrderMapper.toEntity(null));
        assertNull(WorkOrderMapper.toDomain(null));
    }
}
