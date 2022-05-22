package com.adg.api.department.Accounting.mapper;

import com.adg.api.department.Accounting.dal.entity.order.OrderDTO;
import com.adg.api.department.Accounting.dal.entity.order.OrderEntity;
import com.adg.api.department.Accounting.dal.entity.order_product.OrderProductEntity;
import com.adg.api.department.Accounting.dal.entity.order_product.OrderProductId;
import com.merlin.mapper.MerlinMapper;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.19 03:04
 */
public class OrderMapper extends MerlinMapper<OrderEntity> {
    public OrderMapper(Class<OrderEntity> entityClass) {
        super(entityClass);
    }

    public OrderEntity toEntityWithId(OrderDTO orderDTO) {
        OrderEntity orderEntity = this.toEntity(orderDTO);

        for (OrderProductEntity orderProduct : orderEntity.getOrderProducts()) {
            orderProduct.setOrderProductId(
                    OrderProductId
                            .builder()
                            .orderId(orderEntity.getAsyncId())
                            .productId(orderProduct.getTempProductId())
                    .build()
            );
            orderProduct.setOrderEntity(orderEntity);
        }
        return orderEntity;

    }
}
