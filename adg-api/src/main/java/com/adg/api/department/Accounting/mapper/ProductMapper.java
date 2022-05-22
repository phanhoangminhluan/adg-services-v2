package com.adg.api.department.Accounting.mapper;

import com.adg.api.department.Accounting.dal.entity.product.ProductEntity;
import com.merlin.mapper.MerlinMapper;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.18 22:04
 */
public class ProductMapper extends MerlinMapper<ProductEntity> {
    public ProductMapper(Class<ProductEntity> entityClass) {
        super(entityClass);
    }
}
