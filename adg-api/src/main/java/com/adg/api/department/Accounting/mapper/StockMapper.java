package com.adg.api.department.Accounting.mapper;

import com.adg.api.department.Accounting.dal.entity.stock.StockEntity;
import com.merlin.mapper.MerlinMapper;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.18 14:38
 */
public class StockMapper extends MerlinMapper<StockEntity> {

    public StockMapper(Class<StockEntity> entityClass) {
        super(entityClass);
    }
}
