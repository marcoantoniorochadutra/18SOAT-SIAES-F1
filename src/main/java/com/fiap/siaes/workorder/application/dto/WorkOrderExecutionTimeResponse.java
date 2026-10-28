package com.fiap.siaes.workorder.application.dto;

import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.ExecutionTimeMetrics;
import lombok.Builder;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.fiap.siaes.sk.util.FunctionalUtils.getIfNotNullOrDefault;

@Builder
public record WorkOrderExecutionTimeResponse(Instant from, Instant to, int workOrderCount, Long averageMinutes,
                                             Long minMinutes, Long maxMinutes, List<StageResponse> stages) {

    public static WorkOrderExecutionTimeResponse from(Instant from, Instant to, ExecutionTimeMetrics metrics) {
        return WorkOrderExecutionTimeResponse.builder()
                .from(from)
                .to(to)
                .workOrderCount(metrics.workOrderCount())
                .averageMinutes(toMinutes(metrics.averageExecution()))
                .minMinutes(toMinutes(metrics.minExecution()))
                .maxMinutes(toMinutes(metrics.maxExecution()))
                .stages(metrics.averageByStatus().entrySet().stream()
                                .map(entry -> StageResponse.of(entry.getKey(), entry.getValue().toMinutes()))
                                .toList())
                .build();
    }

    private static Long toMinutes(Duration duration) {
        return getIfNotNullOrDefault(duration, Duration::toMinutes, null);
    }

    public record StageResponse(WorkOrderStatus status, long averageMinutes) {

        public static StageResponse of(WorkOrderStatus status, long averageMinutes) {
            return new StageResponse(status, averageMinutes);
        }
    }
}
