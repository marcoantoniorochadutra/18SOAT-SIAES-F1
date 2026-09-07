package com.fiap.siaes.workorder.domain.model.enums;

import lombok.Getter;

@Getter
public enum WorkOrderStatus {

    RECEIVED("Recebida"),
    IN_DIAGNOSIS("Em diagnóstico"),
    AWAITING_APPROVAL("Aguardando aprovação"),
    IN_EXECUTION("Em execução"),
    FINISHED("Finalizada"),
    DELIVERED("Entregue");

    private final String description;

    WorkOrderStatus(String description) {
        this.description = description;
    }
}
