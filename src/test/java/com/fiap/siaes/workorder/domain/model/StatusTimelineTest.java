package com.fiap.siaes.workorder.domain.model;

import com.fiap.siaes.workorder.domain.model.enums.WorkOrderStatus;
import com.fiap.siaes.workorder.domain.model.vo.StatusTimeline;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderStatusHistoryId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static com.fiap.siaes.workorder.WorkOrderTestFactory.RECEIVED_AT;
import static com.fiap.siaes.workorder.WorkOrderTestFactory.createFinishedTimeline;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UnitTest - Domain - Status Timeline")
class StatusTimelineTest {

    @Test
    @DisplayName("Deve calcular o tempo em cada status até a mudança para o status seguinte")
    void shouldCalculateTimeInStatus() {
        StatusTimeline timeline = createFinishedTimeline(30, 60, 240, 120);

        assertEquals(Map.of(WorkOrderStatus.RECEIVED, Duration.ofMinutes(30),
                            WorkOrderStatus.IN_DIAGNOSIS, Duration.ofMinutes(60),
                            WorkOrderStatus.AWAITING_APPROVAL, Duration.ofMinutes(240),
                            WorkOrderStatus.IN_EXECUTION, Duration.ofMinutes(120)), timeline.timeInStatus());
    }

    @Test
    @DisplayName("Deve ordenar o histórico pela data de mudança de status")
    void shouldSortHistoryByUpdatedAt() {
        StatusTimeline timeline = StatusTimeline.of(List.of(history(WorkOrderStatus.IN_DIAGNOSIS, 30),
                                                            history(WorkOrderStatus.RECEIVED, 0),
                                                            history(WorkOrderStatus.AWAITING_APPROVAL, 50)));

        assertEquals(Map.of(WorkOrderStatus.RECEIVED, Duration.ofMinutes(30),
                            WorkOrderStatus.IN_DIAGNOSIS, Duration.ofMinutes(20)), timeline.timeInStatus());
    }

    @Test
    @DisplayName("Deve somar os trechos de um status que aparece mais de uma vez")
    void shouldSumRepeatedStatus() {
        StatusTimeline timeline = StatusTimeline.of(List.of(history(WorkOrderStatus.AWAITING_APPROVAL, 0),
                                                            history(WorkOrderStatus.IN_DIAGNOSIS, 10),
                                                            history(WorkOrderStatus.AWAITING_APPROVAL, 30),
                                                            history(WorkOrderStatus.IN_EXECUTION, 45)));

        assertEquals(Map.of(WorkOrderStatus.AWAITING_APPROVAL, Duration.ofMinutes(25),
                            WorkOrderStatus.IN_DIAGNOSIS, Duration.ofMinutes(20)), timeline.timeInStatus());
    }

    @Test
    @DisplayName("Não deve calcular tempo para o status atual, que ainda não terminou")
    void shouldNotCalculateTimeForCurrentStatus() {
        StatusTimeline timeline = StatusTimeline.of(List.of(history(WorkOrderStatus.RECEIVED, 0)));

        assertTrue(timeline.timeInStatus().isEmpty());
    }

    private static WorkOrderStatusHistory history(WorkOrderStatus status, long minutesAfterReceived) {
        return new WorkOrderStatusHistory(WorkOrderStatusHistoryId.generate(), status, RECEIVED_AT.plus(Duration.ofMinutes(minutesAfterReceived)));
    }
}
