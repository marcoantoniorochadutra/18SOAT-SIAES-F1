package com.fiap.siaes.workorder.domain.model.vo;

import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.workorder.domain.exception.WorkOrderDiagnosisDescriptionRequiredException;

import java.time.Instant;

import static org.apache.commons.lang3.StringUtils.isBlank;

public record Diagnosis(String description, UserId diagnosedBy, Instant diagnosedAt) {

    public Diagnosis {
        if (isBlank(description)) {
            throw new WorkOrderDiagnosisDescriptionRequiredException();
        }

        description = description.strip();
    }

    public static Diagnosis of(String description, UserId diagnosedBy, Instant diagnosedAt) {
        return new Diagnosis(description, diagnosedBy, diagnosedAt);
    }
}
