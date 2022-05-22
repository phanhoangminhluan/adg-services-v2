package com.adg.api.department.Accounting.repository.order_product;

import com.adg.api.department.Accounting.dal.entity.order_product.OrderProductEntity;
import com.adg.api.department.Accounting.dal.entity.order_product.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.19 03:02
 */
@Repository
public interface OrderProductRepository extends JpaRepository<OrderProductEntity, OrderProductId> {
}
