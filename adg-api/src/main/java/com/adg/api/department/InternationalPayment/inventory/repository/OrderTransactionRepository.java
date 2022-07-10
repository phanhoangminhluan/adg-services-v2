package com.adg.api.department.InternationalPayment.inventory.repository;

import com.adg.api.department.InternationalPayment.inventory.entity.OrderTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 04:15
 */
@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, UUID> {

    @Query("SELECT sum(ot.releaseQuantity) FROM OrderTransaction ot WHERE ot.order.id = ?1")
    Double countReleaseQuantityByOrderId(UUID orderId);

}
