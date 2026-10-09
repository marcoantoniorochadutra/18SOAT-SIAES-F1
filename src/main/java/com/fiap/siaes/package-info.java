
@JdbcTypeRegistration(value = UUIDWrapperJdbcType.class)

@JavaTypeRegistration(descriptorClass = WorkOrderIdJavaType.class, javaType = WorkOrderId.class)

@JavaTypeRegistration(descriptorClass = UserIdJavaType.class, javaType = UserId.class)
@JavaTypeRegistration(descriptorClass = UserStatusHistoryIdJavaType.class, javaType = UserStatusHistoryId.class)
@JavaTypeRegistration(descriptorClass = CustomerIdJavaType.class, javaType = CustomerId.class)

@JavaTypeRegistration(descriptorClass = VehicleIdJavaType.class, javaType = VehicleId.class)
@JavaTypeRegistration(descriptorClass = MaintenanceIdJavaType.class, javaType = MaintenanceId.class)
@JavaTypeRegistration(descriptorClass = SuppliesIdJavaType.class, javaType = SuppliesId.class)

package com.fiap.siaes;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.model.vo.CustomerId.CustomerIdJavaType;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId.MaintenanceIdJavaType;
import com.fiap.siaes.sk.infraestructure.persistence.UUIDWrapperJdbcType;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId;
import com.fiap.siaes.supplies.domain.model.vo.SuppliesId.SuppliesIdJavaType;
import com.fiap.siaes.user.domain.model.vo.UserId;
import com.fiap.siaes.user.domain.model.vo.UserId.UserIdJavaType;
import com.fiap.siaes.user.domain.model.vo.UserStatusHistoryId;
import com.fiap.siaes.user.domain.model.vo.UserStatusHistoryId.UserStatusHistoryIdJavaType;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId;
import com.fiap.siaes.vehicle.domain.model.vo.VehicleId.VehicleIdJavaType;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderId.WorkOrderIdJavaType;
import org.hibernate.annotations.JavaTypeRegistration;
import org.hibernate.annotations.JdbcTypeRegistration;
