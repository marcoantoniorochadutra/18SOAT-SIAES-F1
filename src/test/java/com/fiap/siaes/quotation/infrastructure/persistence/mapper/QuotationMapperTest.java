package com.fiap.siaes.quotation.infrastructure.persistence.mapper;

import com.fiap.siaes.quotation.domain.model.Quotation;
import com.fiap.siaes.quotation.domain.model.QuotationMaintenance;
import com.fiap.siaes.quotation.domain.model.QuotationSupplies;
import com.fiap.siaes.quotation.infrastructure.persistence.entity.QuotationJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fiap.siaes.quotation.QuotationTestFactory.createQuotation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("UnitTest - Mapper - Quotation")
class QuotationMapperTest {

    @Test
    @DisplayName("Deve converter o orçamento para entidade e de volta para o domínio")
    void shouldMapQuotationRoundTrip() {
        Quotation quotation = createQuotation();

        QuotationJpaEntity entity = QuotationMapper.toEntity(quotation);
        Quotation mapped = QuotationMapper.toDomain(entity);

        assertEquals(quotation.getId(), mapped.getId());
        assertEquals(quotation.getWorkOrderId(), mapped.getWorkOrderId());
        assertEquals(quotation.getCustomerId(), mapped.getCustomerId());
        assertEquals(quotation.getVehicleId(), mapped.getVehicleId());
        assertEquals(quotation.getDescription(), mapped.getDescription());
        assertEquals(quotation.getOdometer(), mapped.getOdometer());
        assertEquals(quotation.getLastStatus(), mapped.getLastStatus());
        assertEquals(quotation.getCreatedAt(), mapped.getCreatedAt());
        assertEquals(quotation.getStatusHistory().size(), mapped.getStatusHistory().size());
        assertEquals(quotation.getTotalAmount(), mapped.getTotalAmount());

        QuotationSupplies supplies = quotation.getSupplies().getFirst();
        QuotationSupplies mappedSupplies = mapped.getSupplies().getFirst();
        assertEquals(supplies.getId(), mappedSupplies.getId());
        assertEquals(supplies.getSuppliesId(), mappedSupplies.getSuppliesId());
        assertEquals(supplies.getQuantity(), mappedSupplies.getQuantity());

        QuotationMaintenance maintenance = quotation.getMaintenances().getFirst();
        QuotationMaintenance mappedMaintenance = mapped.getMaintenances().getFirst();
        assertEquals(maintenance.getId(), mappedMaintenance.getId());
        assertEquals(maintenance.getMaintenanceId(), mappedMaintenance.getMaintenanceId());
        assertEquals(maintenance.getUnitPrice(), mappedMaintenance.getUnitPrice());
    }

    @Test
    @DisplayName("Deve retornar nulo quando a entrada for nula")
    void shouldReturnNullWhenInputIsNull() {
        assertNull(QuotationMapper.toEntity(null));
        assertNull(QuotationMapper.toDomain(null));
    }
}
