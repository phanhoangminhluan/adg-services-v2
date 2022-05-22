package com.adg.api.department.Accounting.mapper;

import com.adg.api.department.Accounting.dal.entity.customer.CustomerEntity;
import com.merlin.mapper.MerlinMapper;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.18 23:54
 */
public class CustomerMapper extends MerlinMapper<CustomerEntity> {
    public CustomerMapper(Class<CustomerEntity> entityClass) {
        super(entityClass);
    }
}
