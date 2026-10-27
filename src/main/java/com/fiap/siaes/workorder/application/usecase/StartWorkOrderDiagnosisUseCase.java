package com.fiap.siaes.workorder.application.usecase;

import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;

public interface StartWorkOrderDiagnosisUseCase {

    void execute(StartWorkOrderDiagnosisCommand command);

    record StartWorkOrderDiagnosisCommand(WorkOrderId id) {

        public static StartWorkOrderDiagnosisCommand of(WorkOrderId id) {
            return new StartWorkOrderDiagnosisCommand(id);
        }
    }
}
