package com.fiap.siaes.workorder.application.service;

import com.fiap.siaes.workorder.application.dto.WorkOrderExecutionTimeResponse;
import com.fiap.siaes.workorder.application.port.WorkOrderQueryPort;
import com.fiap.siaes.workorder.application.usecase.GetWorkOrderExecutionTimeUseCase;
import com.fiap.siaes.workorder.domain.exception.WorkOrderMetricsPeriodInvalidException;
import com.fiap.siaes.workorder.domain.model.vo.ExecutionTimeMetrics;
import com.fiap.siaes.workorder.domain.model.vo.StatusTimeline;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static java.util.Objects.requireNonNullElseGet;

@Service
@RequiredArgsConstructor
public class GetWorkOrderExecutionTimeAppService implements GetWorkOrderExecutionTimeUseCase {

    private static final Duration DEFAULT_PERIOD = Duration.ofDays(30);

    private final WorkOrderQueryPort workOrderQueryPort;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public WorkOrderExecutionTimeResponse execute(GetWorkOrderExecutionTimeCommand command) {
        Instant to = requireNonNullElseGet(command.to(), Instant::now);
        Instant from = requireNonNullElseGet(command.from(), () -> to.minus(DEFAULT_PERIOD));

        if (from.isAfter(to)) {
            throw new WorkOrderMetricsPeriodInvalidException(from.toString(), to.toString());
        }

        List<StatusTimeline> timelines = this.workOrderQueryPort.findStatusTimelinesOfFinishedBetween(from, to);

        return WorkOrderExecutionTimeResponse.from(from, to, ExecutionTimeMetrics.calculate(timelines));
    }
}
