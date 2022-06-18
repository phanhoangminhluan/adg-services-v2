package com.adg.api.department.Accounting.repository.customer_order_product;

import com.adg.api.department.Accounting.dal.entity.datalake.CustomerOrderProduct.CustomerOrderProductEntity;
import com.adg.api.department.Accounting.dal.entity.datalake.CustomerOrderProduct.CustomerOrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.06.17 17:27
 */
@Repository
public interface CustomerOrderProductRepository extends JpaRepository<CustomerOrderProductEntity, CustomerOrderProductId> {

}
