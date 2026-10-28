package com.fiap.siaes.workorder.domain.model;

import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.ExecutionTimeMetrics;
import com.fiap.siaes.workorder.domain.model.vo.StatusTimeline;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static com.fiap.siaes.workorder.WorkOrderTestFactory.createFinishedTimeline;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - Execution Time Metrics")
class ExecutionTimeMetricsTest {

    @Test
    @DisplayName("Deve calcular média, mínimo e máximo de execução e a média de cada etapa")
    void shouldCalculateMetrics() {
        List<StatusTimeline> timelines = List.of(createFinishedTimeline(30, 60, 240, 120),
                                                 createFinishedTimeline(10, 40, 60, 60));

        ExecutionTimeMetrics metrics = ExecutionTimeMetrics.calculate(timelines);

        assertEquals(2, metrics.workOrderCount());
        assertEquals(Duration.ofMinutes(90), metrics.averageExecution());
        assertEquals(Duration.ofMinutes(60), metrics.minExecution());
        assertEquals(Duration.ofMinutes(120), metrics.maxExecution());
        assertEquals(Map.of(WorkOrderStatus.RECEIVED, Duration.ofMinutes(20),
                            WorkOrderStatus.IN_DIAGNOSIS, Duration.ofMinutes(50),
                            WorkOrderStatus.AWAITING_APPROVAL, Duration.ofMinutes(150),
                            WorkOrderStatus.IN_EXECUTION, Duration.ofMinutes(90)), metrics.averageByStatus());
    }

    @Test
    @DisplayName("Deve retornar métricas vazias quando não houver ordens de serviço finalizadas")
    void shouldReturnEmptyMetricsWithoutWorkOrders() {
        ExecutionTimeMetrics metrics = ExecutionTimeMetrics.calculate(List.of());

        assertEquals(0, metrics.workOrderCount());
        assertNull(metrics.averageExecution());
        assertNull(metrics.minExecution());
        assertNull(metrics.maxExecution());
        assertTrue(metrics.averageByStatus().isEmpty());
    }
}
