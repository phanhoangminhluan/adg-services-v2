package com.adg.api.department.InternationalPayment.inventory.repository;

import com.adg.api.department.InternationalPayment.inventory.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:50
 */
@Repository
public interface CrmOrderRepository extends JpaRepository<Order, UUID> {

}
