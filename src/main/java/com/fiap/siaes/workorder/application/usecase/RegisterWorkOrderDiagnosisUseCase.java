package com.fiap.siaes.workorder.application.usecase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiap.siaes.auth.application.dto.AuthenticatedUser;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public interface RegisterWorkOrderDiagnosisUseCase {

    void execute(RegisterWorkOrderDiagnosisCommand command);

    @Builder(toBuilder = true)
    record RegisterWorkOrderDiagnosisCommand(
            @JsonIgnore WorkOrderId id,
            @JsonIgnore AuthenticatedUser authenticatedUser,

            @NotBlank(message = "{WorkOrderDiagnosisCommand.description.notBlank}")
            @Size(message = "{WorkOrderDiagnosisCommand.description.size.max}", max = 2000)
            String description) {

        public RegisterWorkOrderDiagnosisCommand with(WorkOrderId id, AuthenticatedUser authenticatedUser) {
            return this.toBuilder()
                    .id(id)
                    .authenticatedUser(authenticatedUser)
                    .build();
        }
    }
}
