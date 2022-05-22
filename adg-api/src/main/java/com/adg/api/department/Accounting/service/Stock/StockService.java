package com.adg.api.department.Accounting.service.Stock;

import com.adg.api.department.Accounting.dal.entity.stock.StockDTO;
import com.adg.api.department.Accounting.dal.entity.stock.StockEntity;
import com.adg.api.department.Accounting.enums.MisaModel;
import com.adg.api.department.Accounting.mapper.StockMapper;
import com.adg.api.department.Accounting.repository.stock.StockRepository;
import com.adg.api.department.Accounting.service.AbstractMisaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.21 22:13
 */
@Service
public class StockService extends AbstractMisaService<StockDTO, StockEntity, String, StockMapper, StockRepository> {

    @Autowired
    public StockService(StockMapper mapper, StockRepository repository) {
        super(mapper, repository);
    }

    protected MisaModel getMisaModel() {
        return MisaModel.STOCK;
    }

}
