package com.adg.api.department.Accounting.repository.order;

import com.adg.api.department.Accounting.dal.entity.order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.19 03:02
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {


    @Query(
        value = "SELECT " +
                "   c.id AS customer_id," +
                "   o.async_id AS order_id," +
                "   op.product_id AS product_id," +
                "   DATE((o.sale_order_date AT TIME ZONE 'UTC') AT TIME ZONE 'WAST') AS sale_order_date," +
                "   sale_order_no," +
                "   status," +
                "   revenue_status_id_text," +
                "   product_name," +
                "   price," +
                "   price_after_tax" +
                "   " +
                "FROM \"order\" o " +
                "RIGHT JOIN order_product op ON o.async_id = op.order_id " +
                "INNER JOIN customer c ON o.account_id = c.id",
            nativeQuery = true
    )
    List<Map<String, Object>> getCustomTable();

}
