package com.fiap.siaes.workorder.domain.model.vo;

import com.fiap.siaes.workorder.domain.model.WorkOrderStatusHistory;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;

import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public record StatusTimeline(List<WorkOrderStatusHistory> history) {

    public StatusTimeline {
        history = history.stream()
                .sorted(Comparator.comparing(WorkOrderStatusHistory::getUpdatedAt))
                .toList();
    }

    public static StatusTimeline of(Collection<WorkOrderStatusHistory> history) {
        return new StatusTimeline(List.copyOf(history));
    }

    public Map<WorkOrderStatus, Duration> timeInStatus() {
        Map<WorkOrderStatus, Duration> timeInStatus = new EnumMap<>(WorkOrderStatus.class);


        WorkOrderStatusHistory previous = null;
        for (WorkOrderStatusHistory statusHistory : this.history) {
            if (isNull(previous)) {
                previous = statusHistory;
                continue;
            }

            Duration elapsed = Duration.between(previous.getUpdatedAt(), statusHistory.getUpdatedAt());
            timeInStatus.merge(previous.getStatus(), elapsed, Duration::plus);

            previous = statusHistory;
        }

        return timeInStatus;
    }
}
