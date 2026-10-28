package com.fiap.siaes.workorder.domain.model.vo;

import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import lombok.Builder;

import java.time.Duration;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.averagingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingLong;

@Builder
public record ExecutionTimeMetrics(int workOrderCount, Duration averageExecution, Duration minExecution,
                                   Duration maxExecution, Map<WorkOrderStatus, Duration> averageByStatus) {

    public static ExecutionTimeMetrics calculate(Collection<StatusTimeline> timelines) {
        List<Map<WorkOrderStatus, Duration>> timesInStatus = timelines.stream()
                .map(StatusTimeline::timeInStatus)
                .toList();

        LongSummaryStatistics execution = timesInStatus.stream()
                .map(timeInStatus -> timeInStatus.get(WorkOrderStatus.IN_EXECUTION))
                .filter(Objects::nonNull)
                .collect(summarizingLong(Duration::toMillis));

        Map<WorkOrderStatus, Duration> averageByStatus = timesInStatus.stream()
                .flatMap(timeInStatus -> timeInStatus.entrySet().stream())
                .collect(groupingBy(Map.Entry::getKey, () -> new EnumMap<>(WorkOrderStatus.class),
                                    collectingAndThen(averagingLong(entry -> entry.getValue().toMillis()), ExecutionTimeMetrics::ofMillis)));

        boolean hasExecution = execution.getCount() > 0;

        return ExecutionTimeMetrics.builder()
                .workOrderCount(timelines.size())
                .averageExecution(hasExecution ? ofMillis(execution.getAverage()) : null)
                .minExecution(hasExecution ? Duration.ofMillis(execution.getMin()) : null)
                .maxExecution(hasExecution ? Duration.ofMillis(execution.getMax()) : null)
                .averageByStatus(averageByStatus)
                .build();
    }

    private static Duration ofMillis(double millis) {
        return Duration.ofMillis(Math.round(millis));
    }
}
