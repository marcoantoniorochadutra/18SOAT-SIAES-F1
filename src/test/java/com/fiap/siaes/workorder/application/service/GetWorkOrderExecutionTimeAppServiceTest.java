package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.workorder.application.dto.WorkOrderExecutionTimeResponse;
import com.fiap.siaes.workorder.application.dto.WorkOrderExecutionTimeResponse.StageResponse;
import com.fiap.siaes.workorder.application.port.WorkOrderQueryPort;
import com.fiap.siaes.workorder.application.usecase.GetWorkOrderExecutionTimeUseCase.GetWorkOrderExecutionTimeCommand;
import com.fiap.siaes.workorder.domain.exception.WorkOrderMetricsPeriodInvalidException;
import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.fiap.siaes.utils.TestUtils.assertExceptionParameters;
import static com.fiap.siaes.workorder.WorkOrderTestFactory.createFinishedTimeline;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnitTest - App Service - Get Work Order Execution Time")
class GetWorkOrderExecutionTimeAppServiceTest {

    @Mock
    private WorkOrderQueryPort workOrderQueryPort;

    @InjectMocks
    private GetWorkOrderExecutionTimeAppService getWorkOrderExecutionTimeAppService;

    @Test
    @DisplayName("Deve calcular o tempo de execução das ordens de serviço finalizadas no período")
    void shouldCalculateExecutionTimeInPeriod() {
        var from = Instant.parse("2026-10-01T00:00:00Z");
        var to = Instant.parse("2026-10-31T23:59:59Z");

        when(this.workOrderQueryPort.findStatusTimelinesOfFinishedBetween(from, to))
                .thenReturn(List.of(createFinishedTimeline(30, 60, 240, 120), createFinishedTimeline(10, 40, 60, 60)));

        WorkOrderExecutionTimeResponse response = this.getWorkOrderExecutionTimeAppService.execute(GetWorkOrderExecutionTimeCommand.of(from, to));

        assertEquals(from, response.from());
        assertEquals(to, response.to());
        assertEquals(2, response.workOrderCount());
        assertEquals(90L, response.averageMinutes());
        assertEquals(60L, response.minMinutes());
        assertEquals(120L, response.maxMinutes());
        assertEquals(List.of(StageResponse.of(WorkOrderStatus.RECEIVED, 20),
                             StageResponse.of(WorkOrderStatus.IN_DIAGNOSIS, 50),
                             StageResponse.of(WorkOrderStatus.AWAITING_APPROVAL, 150),
                             StageResponse.of(WorkOrderStatus.IN_EXECUTION, 90)), response.stages());
    }

    @Test
    @DisplayName("Deve considerar os últimos 30 dias quando o período não for informado")
    void shouldUseLastThirtyDaysByDefault() {
        ArgumentCaptor<Instant> fromCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> toCaptor = ArgumentCaptor.forClass(Instant.class);
        var before = Instant.now();

        when(this.workOrderQueryPort.findStatusTimelinesOfFinishedBetween(fromCaptor.capture(), toCaptor.capture())).thenReturn(List.of());

        WorkOrderExecutionTimeResponse response = this.getWorkOrderExecutionTimeAppService.execute(GetWorkOrderExecutionTimeCommand.of(null, null));

        assertEquals(Duration.ofDays(30), Duration.between(fromCaptor.getValue(), toCaptor.getValue()));
        assertFalse(toCaptor.getValue().isBefore(before));
        assertEquals(0, response.workOrderCount());
        assertNull(response.averageMinutes());
        assertTrue(response.stages().isEmpty());
    }

    @Test
    @DisplayName("Deve considerar 30 dias antes do fim quando só o fim for informado")
    void shouldUseThirtyDaysBeforeEndWhenOnlyEndIsInformed() {
        var to = Instant.parse("2026-10-31T00:00:00Z");

        when(this.workOrderQueryPort.findStatusTimelinesOfFinishedBetween(Instant.parse("2026-10-01T00:00:00Z"), to)).thenReturn(List.of());

        WorkOrderExecutionTimeResponse response = this.getWorkOrderExecutionTimeAppService.execute(GetWorkOrderExecutionTimeCommand.of(null, to));

        assertEquals(Instant.parse("2026-10-01T00:00:00Z"), response.from());
        verify(this.workOrderQueryPort).findStatusTimelinesOfFinishedBetween(response.from(), to);
    }

    @Test
    @DisplayName("Não deve calcular quando o início do período for posterior ao fim")
    void shouldNotCalculateWhenStartIsAfterEnd() {
        var from = Instant.parse("2026-11-01T00:00:00Z");
        var to = Instant.parse("2026-10-01T00:00:00Z");
        var command = GetWorkOrderExecutionTimeCommand.of(from, to);

        var exception = assertThrows(WorkOrderMetricsPeriodInvalidException.class,
                                     () -> this.getWorkOrderExecutionTimeAppService.execute(command));

        assertExceptionParameters(exception, from.toString(), to.toString());
        verifyNoInteractions(this.workOrderQueryPort);
    }
}
