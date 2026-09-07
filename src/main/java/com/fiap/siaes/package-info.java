
@JdbcTypeRegistration(value = UUIDWrapperJdbcType.class)

@JavaTypeRegistration(descriptorClass = WorkOrderIdJavaType.class, javaType = WorkOrderId.class)

@JavaTypeRegistration(descriptorClass = UserIdJavaType.class, javaType = UserId.class)
@JavaTypeRegistration(descriptorClass = CustomerIdJavaType.class, javaType = CustomerId.class)

@JavaTypeRegistration(descriptorClass = VehicleIdJavaType.class, javaType = VehicleId.class)
@JavaTypeRegistration(descriptorClass = MaintenanceIdJavaType.class, javaType = MaintenanceId.class)
@JavaTypeRegistration(descriptorClass = SuppliesIdJavaType.class, javaType = SuppliesId.class)

package com.fiap.siaes;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.customer.domain.model.CustomerId.CustomerIdJavaType;
import com.fiap.siaes.maintenance.domain.model.MaintenanceId;
import com.fiap.siaes.maintenance.domain.model.MaintenanceId.MaintenanceIdJavaType;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJdbcType;
import com.fiap.siaes.supplies.domain.model.SuppliesId;
import com.fiap.siaes.supplies.domain.model.SuppliesId.SuppliesIdJavaType;
import com.fiap.siaes.user.domain.model.UserId;
import com.fiap.siaes.user.domain.model.UserId.UserIdJavaType;
import com.fiap.siaes.vehicle.domain.model.VehicleId;
import com.fiap.siaes.vehicle.domain.model.VehicleId.VehicleIdJavaType;
import com.fiap.siaes.workorder.domain.model.WorkOrderId;
import com.fiap.siaes.workorder.domain.model.WorkOrderId.WorkOrderIdJavaType;
import org.hibernate.annotations.JavaTypeRegistration;
import org.hibernate.annotations.JdbcTypeRegistration;
