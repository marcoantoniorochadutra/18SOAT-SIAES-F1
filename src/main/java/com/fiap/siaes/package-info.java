
@JdbcTypeRegistration(value = UUIDWrapperJdbcType.class)

@JavaTypeRegistration(descriptorClass = WorkOrderIdJavaType.class, javaType = WorkOrderId.class)
@JavaTypeRegistration(descriptorClass = WorkOrderStatusHistoryIdJavaType.class, javaType = WorkOrderStatusHistoryId.class)

@JavaTypeRegistration(descriptorClass = UserIdJavaType.class, javaType = UserId.class)
@JavaTypeRegistration(descriptorClass = UserStatusHistoryIdJavaType.class, javaType = UserStatusHistoryId.class)
@JavaTypeRegistration(descriptorClass = CustomerIdJavaType.class, javaType = CustomerId.class)

@JavaTypeRegistration(descriptorClass = VehicleIdJavaType.class, javaType = VehicleId.class)
@JavaTypeRegistration(descriptorClass = MaintenanceIdJavaType.class, javaType = MaintenanceId.class)
@JavaTypeRegistration(descriptorClass = SuppliesIdJavaType.class, javaType = SuppliesId.class)
@JavaTypeRegistration(descriptorClass = StockMovementIdJavaType.class, javaType = StockMovementId.class)
@JavaTypeRegistration(descriptorClass = QuotationIdJavaType.class, javaType = QuotationId.class)
@JavaTypeRegistration(descriptorClass = QuotationSuppliesIdJavaType.class, javaType = QuotationSuppliesId.class)
@JavaTypeRegistration(descriptorClass = QuotationMaintenanceIdJavaType.class, javaType = QuotationMaintenanceId.class)
@JavaTypeRegistration(descriptorClass = QuotationStatusHistoryIdJavaType.class, javaType = QuotationStatusHistoryId.class)
@JavaTypeRegistration(descriptorClass = QuotationApprovalTokenIdJavaType.class, javaType = QuotationApprovalTokenId.class)

package com.fiap.siaes;

import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.model.vo.CustomerId.CustomerIdJavaType;
import com.fiap.siaes.inventory.domain.model.vo.StockMovementId;
import com.fiap.siaes.inventory.domain.model.vo.StockMovementId.StockMovementIdJavaType;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId;
import com.fiap.siaes.maintenance.domain.model.vo.MaintenanceId.MaintenanceIdJavaType;
import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationApprovalTokenId.QuotationApprovalTokenIdJavaType;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationId.QuotationIdJavaType;
import com.fiap.siaes.quotation.domain.model.vo.QuotationMaintenanceId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationMaintenanceId.QuotationMaintenanceIdJavaType;
import com.fiap.siaes.quotation.domain.model.vo.QuotationStatusHistoryId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationStatusHistoryId.QuotationStatusHistoryIdJavaType;
import com.fiap.siaes.quotation.domain.model.vo.QuotationSuppliesId;
import com.fiap.siaes.quotation.domain.model.vo.QuotationSuppliesId.QuotationSuppliesIdJavaType;
import com.fiap.siaes.sk.infrastructure.persistence.UUIDWrapperJdbcType;
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
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderStatusHistoryId;
import com.fiap.siaes.workorder.domain.model.vo.WorkOrderStatusHistoryId.WorkOrderStatusHistoryIdJavaType;
import org.hibernate.annotations.JavaTypeRegistration;
import org.hibernate.annotations.JdbcTypeRegistration;
