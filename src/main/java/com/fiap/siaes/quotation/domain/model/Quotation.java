package com.fiap.siaes.quotation.domain.model;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.quotation.domain.event.QuotationApprovedEvent;
import com.fiap.siaes.quotation.domain.event.QuotationCompletedEvent;
import com.fiap.siaes.quotation.domain.event.QuotationRejectedEvent;
import com.fiap.siaes.quotation.domain.exception.QuotationDuplicatedItemException;
import com.fiap.siaes.quotation.domain.exception.QuotationItemsRequiredException;
import com.fiap.siaes.quotation.domain.exception.QuotationNotAwaitingApprovalException;
import com.fiap.siaes.quotation.domain.exception.QuotationNotEditableException;
import com.fiap.siaes.quotation.domain.exception.QuotationOdometerInvalidException;
import com.fiap.siaes.quotation.domain.model.enums.QuotationDecisionChannel;
import com.fiap.siaes.quotation.domain.model.enums.QuotationStatus;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.sk.domain.model.AggregateRoot;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.trimToNull;

@Getter
public class Quotation extends AggregateRoot {

    private QuotationId id;
    private WorkOrderId workOrderId;
    private CustomerId customerId;
    private VehicleId vehicleId;
    private String description;
    private long odometer;

    private QuotationStatus lastStatus;
    private Set<QuotationStatusHistory> statusHistory;

    private List<QuotationSupplies> supplies;
    private List<QuotationMaintenance> maintenances;

    private QuotationDecisionChannel decisionChannel;
    private UserId decidedBy;
    private Instant decidedAt;
    private String rejectionReason;

    private Instant createdAt;
    private Long version;

    @Builder
    public Quotation(WorkOrderId workOrderId, CustomerId customerId, VehicleId vehicleId, String description, long odometer,
                     List<QuotationSupplies> supplies, List<QuotationMaintenance> maintenances) {
        this.id = QuotationId.generate();
        this.workOrderId = workOrderId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.description = trimToNull(description);
        this.odometer = this.validateOdometer(odometer);
        this.changeItems(supplies, maintenances);
        this.createdAt = Instant.now();
        this.changeStatus(QuotationStatus.DRAFT);
    }

    @Builder(builderMethodName = "recreate", builderClassName = "RecreateBuilder")
    public Quotation(QuotationId id, WorkOrderId workOrderId, CustomerId customerId, VehicleId vehicleId, String description,
                     long odometer, QuotationStatus lastStatus, Set<QuotationStatusHistory> statusHistory,
                     List<QuotationSupplies> supplies, List<QuotationMaintenance> maintenances,
                     QuotationDecisionChannel decisionChannel, UserId decidedBy, Instant decidedAt, String rejectionReason,
                     Instant createdAt, Long version) {
        this.id = id;
        this.workOrderId = workOrderId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.description = description;
        this.odometer = odometer;
        this.lastStatus = lastStatus;
        this.statusHistory = statusHistory;
        this.supplies = supplies;
        this.maintenances = maintenances;
        this.decisionChannel = decisionChannel;
        this.decidedBy = decidedBy;
        this.decidedAt = decidedAt;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.version = version;
    }

    public void complete() {
        this.requireDraft();
        this.changeStatus(QuotationStatus.AWAITING_APPROVAL);

        this.registerEvent(QuotationCompletedEvent.of(this.id, this.workOrderId, Instant.now()));
    }

    public void discard() {
        this.requireDraft();
        this.changeStatus(QuotationStatus.DISCARDED);
    }

    public void approve(QuotationDecisionChannel channel, UserId decidedBy) {
        this.decide(QuotationStatus.APPROVED, channel, decidedBy);

        this.registerEvent(QuotationApprovedEvent.of(this.id, this.workOrderId, this.decidedAt));
    }

    public void reject(QuotationDecisionChannel channel, UserId decidedBy, String reason) {
        this.decide(QuotationStatus.REJECTED, channel, decidedBy);
        this.rejectionReason = trimToNull(reason);

        this.registerEvent(QuotationRejectedEvent.of(this.id, this.workOrderId, this.decidedAt));
    }

    public void update(String description, long odometer, List<QuotationSupplies> supplies, List<QuotationMaintenance> maintenances) {
        this.requireDraft();

        long validOdometer = this.validateOdometer(odometer);

        this.changeItems(supplies, maintenances);
        this.description = trimToNull(description);
        this.odometer = validOdometer;
    }

    public BigDecimal getTotalAmount() {
        return Stream.concat(this.supplies.stream().map(QuotationSupplies::getSubtotal),
                             this.maintenances.stream().map(QuotationMaintenance::getSubtotal))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<SuppliesId, Integer> getSuppliesQuantities() {
        return this.supplies.stream()
                .collect(Collectors.toMap(QuotationSupplies::getSuppliesId, QuotationSupplies::getQuantity));
    }

    public boolean isOwnedBy(CustomerId customerId) {
        return this.customerId.equals(customerId);
    }

    public boolean isDraft() {
        return !this.lastStatus.isNotDraft();
    }

    public boolean isAwaitingApproval() {
        return !this.lastStatus.isNotAwaitingApproval();
    }

    public boolean holdsStockReservation() {
        return this.isDraft() || this.isAwaitingApproval();
    }

    public void ensureAwaitingApproval() {
        if (!this.isAwaitingApproval())
            throw new QuotationNotAwaitingApprovalException(this.id.toString(), this.lastStatus.name());
    }

    private void requireDraft() {
        if (this.lastStatus.isNotDraft())
            throw new QuotationNotEditableException(this.id.toString(), this.lastStatus.name());
    }

    private void changeItems(List<QuotationSupplies> supplies, List<QuotationMaintenance> maintenances) {
        List<QuotationSupplies> newSupplies = isNull(supplies) ? List.of() : supplies;
        List<QuotationMaintenance> newMaintenances = isNull(maintenances) ? List.of() : maintenances;

        if (newSupplies.isEmpty() && newMaintenances.isEmpty())
            throw new QuotationItemsRequiredException();

        this.validateNoDuplicates(newSupplies, QuotationSupplies::getSuppliesId);
        this.validateNoDuplicates(newMaintenances, QuotationMaintenance::getMaintenanceId);

        this.supplies = new ArrayList<>(newSupplies);
        this.maintenances = new ArrayList<>(newMaintenances);
    }

    private <T> void validateNoDuplicates(List<T> items, Function<T, Object> itemId) {
        Set<Object> ids = new HashSet<>();

        for (T item : items) {
            Object referenceId = itemId.apply(item);

            if (!ids.add(referenceId)) {
                throw new QuotationDuplicatedItemException(referenceId.toString());
            }
        }
    }

    private long validateOdometer(long odometer) {
        if (odometer < 0) {
            throw new QuotationOdometerInvalidException();
        }

        return odometer;
    }

    private void decide(QuotationStatus status, QuotationDecisionChannel channel, UserId decidedBy) {
        this.ensureAwaitingApproval();

        this.decisionChannel = channel;
        this.decidedBy = decidedBy;
        this.decidedAt = Instant.now();
        this.changeStatus(status);
    }

    private void changeStatus(QuotationStatus newStatus) {
        this.lastStatus = newStatus;

        if (isNull(this.statusHistory))
            this.statusHistory = new HashSet<>();

        this.statusHistory.add(QuotationStatusHistory.of(newStatus));
    }

    public Set<QuotationStatusHistory> getStatusHistory() {
        return isNull(this.statusHistory) ? Set.of() : Collections.unmodifiableSet(this.statusHistory);
    }

    public List<QuotationSupplies> getSupplies() {
        return isNull(this.supplies) ? List.of() : Collections.unmodifiableList(this.supplies);
    }

    public List<QuotationMaintenance> getMaintenances() {
        return isNull(this.maintenances) ? List.of() : Collections.unmodifiableList(this.maintenances);
    }
}
