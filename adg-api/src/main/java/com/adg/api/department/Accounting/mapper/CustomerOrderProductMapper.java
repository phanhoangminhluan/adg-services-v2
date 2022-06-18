package com.adg.api.department.Accounting.mapper;

import com.adg.api.department.Accounting.dal.entity.datalake.CustomerOrderProduct.CustomerOrderProductEntity;
import com.merlin.mapper.MerlinMapper;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.06.18 06:19
 */
public class CustomerOrderProductMapper extends MerlinMapper<CustomerOrderProductEntity> {
    public CustomerOrderProductMapper(Class<CustomerOrderProductEntity> entityClass) {
        super(entityClass);
    }
}
