package com.adg.api.department.Accounting.mapper;

import com.adg.api.department.Accounting.dal.entity.order_product.OrderProductEntity;
import com.merlin.mapper.MerlinMapper;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.19 03:05
 */
public class OrderProductMapper extends MerlinMapper<OrderProductEntity> {
    public OrderProductMapper(Class<OrderProductEntity> entityClass) {
        super(entityClass);
    }
}
