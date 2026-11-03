package com.fiap.siaes.sk.infrastructure.metrics;

import com.fiap.siaes.sk.domain.event.DomainEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DomainEventMetricsSubscriber {

    static final String METRIC_NAME = "siaes.domain.events";
    static final String EVENT_TAG = "event";

    private final MeterRegistry meterRegistry;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(DomainEvent event) {
        Counter.builder(METRIC_NAME)
                .description("Domain events published after a committed transaction")
                .tag(EVENT_TAG, event.getClass().getSimpleName())
                .register(this.meterRegistry)
                .increment();
    }
}
