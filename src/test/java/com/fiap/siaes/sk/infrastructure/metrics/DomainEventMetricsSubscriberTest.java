package com.fiap.siaes.sk.infrastructure.metrics;

import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.workorder.domain.event.WorkOrderCancelledEvent;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static com.fiap.siaes.inventory.InventoryTestFactory.createStockMovedEvent;
import static com.fiap.siaes.sk.infrastructure.metrics.DomainEventMetricsSubscriber.EVENT_TAG;
import static com.fiap.siaes.sk.infrastructure.metrics.DomainEventMetricsSubscriber.METRIC_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - Subscriber - Domain Event Metrics")
class DomainEventMetricsSubscriberTest {

    @Spy
    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    @InjectMocks
    private DomainEventMetricsSubscriber domainEventMetricsSubscriber;

    @Test
    @DisplayName("Deve contar cada evento de domínio pelo tipo")
    void shouldCountEventsByType() {
        var cancelled = WorkOrderCancelledEvent.of(new WorkOrderId(UUID.randomUUID()), new UserId(UUID.randomUUID()), Instant.now());

        this.domainEventMetricsSubscriber.on(cancelled);
        this.domainEventMetricsSubscriber.on(cancelled);
        this.domainEventMetricsSubscriber.on(createStockMovedEvent());

        assertEquals(2.0, this.countOf("WorkOrderCancelledEvent"));
        assertEquals(1.0, this.countOf("StockMovedEvent"));
    }

    @Test
    @DisplayName("Não deve registrar contador para tipo de evento que não ocorreu")
    void shouldNotRegisterUnseenEventTypes() {
        this.domainEventMetricsSubscriber.on(createStockMovedEvent());

        assertNull(this.meterRegistry.find(METRIC_NAME).tag(EVENT_TAG, "QuotationApprovedEvent").counter());
    }

    private double countOf(String eventType) {
        return this.meterRegistry.get(METRIC_NAME).tag(EVENT_TAG, eventType).counter().count();
    }
}
