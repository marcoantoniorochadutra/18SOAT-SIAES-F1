package com.fiap.siaes.workorder.infrastructure.persistence.entity;

import com.fiap.siaes.workorder.domain.model.WorkOrderId;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderJpa {

    private WorkOrderId id;

}
