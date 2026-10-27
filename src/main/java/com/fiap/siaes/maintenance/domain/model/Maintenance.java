package com.fiap.siaes.maintenance.domain.model;

import com.fiap.siaes.maintenance.domain.exception.MaintenanceAlreadyActiveException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceAlreadyInactiveException;
import com.fiap.siaes.maintenance.domain.exception.MaintenanceEstimatedDurationInvalidException;
import com.fiap.siaes.maintenance.domain.exception.MaintenancePriceInvalidException;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceCode;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

@Getter
public class Maintenance {

    private MaintenanceId id;
    private MaintenanceCode code;
    private String description;
    private BigDecimal price;
    private int estimatedDurationMinutes;
    private boolean active;

    @Builder
    public Maintenance(String code, String description, BigDecimal price, int estimatedDurationMinutes) {
        this.id = MaintenanceId.generate();
        this.code = MaintenanceCode.create(code);
        this.description = description;
        this.price = this.validatePrice(price);
        this.estimatedDurationMinutes = this.validateEstimatedDuration(estimatedDurationMinutes);
        this.active = true;
    }

    @Builder(builderMethodName = "recreate", builderClassName = "RecreateBuilder")
    public Maintenance(MaintenanceId id, MaintenanceCode code, String description, BigDecimal price,
                       int estimatedDurationMinutes, boolean active) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.price = price;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.active = active;
    }

    public void update(String code, String description, BigDecimal price, int estimatedDurationMinutes) {
        BigDecimal validPrice = this.validatePrice(price);
        int validEstimatedDuration = this.validateEstimatedDuration(estimatedDurationMinutes);

        this.code = MaintenanceCode.create(code);
        this.description = description;
        this.price = validPrice;
        this.estimatedDurationMinutes = validEstimatedDuration;
    }

    public void deactivate() {
        if (!this.active)
            throw new MaintenanceAlreadyInactiveException(this.id.toString());

        this.active = false;
    }

    public void activate() {
        if (this.active)
            throw new MaintenanceAlreadyActiveException(this.id.toString());

        this.active = true;
    }

    public boolean isDeactivated() {
        return !this.active;
    }

    private BigDecimal validatePrice(BigDecimal price) {
        if (isNull(price) || price.signum() < 0) {
            throw new MaintenancePriceInvalidException();
        }

        return price;
    }

    private int validateEstimatedDuration(int estimatedDurationMinutes) {
        if (estimatedDurationMinutes <= 0) {
            throw new MaintenanceEstimatedDurationInvalidException();
        }

        return estimatedDurationMinutes;
    }
}
