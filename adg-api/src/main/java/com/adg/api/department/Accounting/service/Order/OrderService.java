package com.adg.api.department.Accounting.service.Order;

import com.adg.api.department.Accounting.dal.entity.order.OrderDTO;
import com.adg.api.department.Accounting.dal.entity.order.OrderEntity;
import com.adg.api.department.Accounting.enums.MisaModel;
import com.adg.api.department.Accounting.mapper.OrderMapper;
import com.adg.api.department.Accounting.repository.order.OrderRepository;
import com.adg.api.department.Accounting.service.AbstractMisaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.22 16:43
 */
@Service
public class OrderService extends AbstractMisaService<OrderDTO, OrderEntity, String, OrderMapper, OrderRepository> {

    @Autowired
    public OrderService(OrderMapper mapper, OrderRepository repository) {
        super(mapper, repository);
    }

    @Override
    protected MisaModel getMisaModel() {
        return MisaModel.ORDER;
    }
}
