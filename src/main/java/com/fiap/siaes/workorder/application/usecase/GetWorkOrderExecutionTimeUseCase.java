package com.fiap.siaes.workorder.application.usecase;

import com.fiap.siaes.workorder.application.dto.WorkOrderExecutionTimeResponse;

import java.time.Instant;

public interface GetWorkOrderExecutionTimeUseCase {

    WorkOrderExecutionTimeResponse execute(GetWorkOrderExecutionTimeCommand command);

    record GetWorkOrderExecutionTimeCommand(Instant from, Instant to) {
        public static GetWorkOrderExecutionTimeCommand of(Instant from, Instant to) {
            return new GetWorkOrderExecutionTimeCommand(from, to);
        }
    }
}
