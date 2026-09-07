//package com.fiap.siaes.vehicle.infrastructure.persistence.repository;
//
//import com.fiap.siaes.customer.domain.model.CustomerId;
//import com.fiap.siaes.vehicle.domain.model.VehicleId;
//import com.fiap.siaes.vehicle.infrastructure.persistence.entity.VehicleJpa;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface VehicleJpaRepository extends JpaRepository<VehicleJpa, VehicleId> {
//
//    List<VehicleJpa> findByOwnerId(CustomerId ownerId);
//
//    boolean existsByPlate(String plate);
//}
