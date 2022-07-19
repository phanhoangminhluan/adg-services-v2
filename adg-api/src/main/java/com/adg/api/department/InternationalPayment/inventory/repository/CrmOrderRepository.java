package com.adg.api.department.InternationalPayment.inventory.repository;

import com.adg.api.department.InternationalPayment.inventory.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:50
 */
@Repository
public interface CrmOrderRepository extends JpaRepository<Order, UUID> {

    @Query(value = "SELECT o FROM Order o WHERE o.port.name = :name")
    Page<Order> getOrdersByPortName(String name, Pageable pageable);

    Optional<Order> findByContractIdAndProductId(String contractId, String productId);

    @Query(value = "SELECT o FROM Order o WHERE o.port.id = :portId")
    Page<Order> getOrdersByPortId(UUID portId, Pageable pageable);
}
