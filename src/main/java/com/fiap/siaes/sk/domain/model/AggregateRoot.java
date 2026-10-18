package com.fiap.siaes.sk.domain.model;

import com.fiap.siaes.sk.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;


public abstract class AggregateRoot {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }
}
