package com.fiap.siaes.supplies.domain.model;

import com.fiap.siaes.sk.domain.model.AggregateRoot;
import com.fiap.siaes.supplies.domain.event.SuppliesCreatedEvent;
import com.fiap.siaes.supplies.domain.exception.SuppliesAlreadyActiveException;
import com.fiap.siaes.supplies.domain.exception.SuppliesAlreadyInactiveException;
import com.fiap.siaes.supplies.domain.exception.SuppliesDescriptionRequiredException;
import com.fiap.siaes.supplies.domain.exception.SuppliesUnitPriceInvalidException;
import com.fiap.siaes.supplies.domain.model.vo.Sku;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
public class Supplies extends AggregateRoot {

    private SuppliesId id;
    private Sku sku;
    private String description;
    private BigDecimal unitPrice;
    private boolean active;

    @Builder
    public Supplies(String sku, String description, BigDecimal unitPrice, int initialStock, UserId createdBy) {
        this.id = SuppliesId.generate();
        this.sku = Sku.create(sku);
        this.description = this.validateDescription(description);
        this.unitPrice = this.validateUnitPrice(unitPrice);
        this.active = true;

        this.registerCreatedEvent(initialStock, createdBy);
    }

    @Builder(builderMethodName = "recreate", builderClassName = "RecreateBuilder")
    public Supplies(SuppliesId id, Sku sku, String description, BigDecimal unitPrice, boolean active) {
        this.id = id;
        this.sku = sku;
        this.description = description;
        this.unitPrice = unitPrice;
        this.active = active;
    }

    public void update(String sku, String description, BigDecimal unitPrice) {
        String validDescription = this.validateDescription(description);
        BigDecimal validUnitPrice = this.validateUnitPrice(unitPrice);

        this.sku = Sku.create(sku);
        this.description = validDescription;
        this.unitPrice = validUnitPrice;
    }

    public void deactivate() {
        if (!this.active) {
            throw new SuppliesAlreadyInactiveException(this.id.toString());
        }

        this.active = false;
    }

    public void activate() {
        if (this.active) {
            throw new SuppliesAlreadyActiveException(this.id.toString());
        }

        this.active = true;
    }

    public boolean isDeactivated() {
        return !this.active;
    }

    private String validateDescription(String description) {
        if (isBlank(description)) {
            throw new SuppliesDescriptionRequiredException();
        }

        return description.trim();
    }

    private BigDecimal validateUnitPrice(BigDecimal unitPrice) {
        if (isNull(unitPrice) || unitPrice.signum() < 0) {
            throw new SuppliesUnitPriceInvalidException();
        }

        return unitPrice;
    }

    private void registerCreatedEvent(int initialStock, UserId createdBy) {
        this.registerEvent(
                SuppliesCreatedEvent.of(this.id, initialStock, createdBy, Instant.now()));
    }
}
