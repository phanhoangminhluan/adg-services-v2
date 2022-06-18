package com.adg.api.department.Accounting.service.CustomerOrderProduct;

import com.adg.api.department.Accounting.dal.entity.datalake.CustomerOrderProduct.CustomerOrderProductDTO;
import com.adg.api.department.Accounting.dal.entity.datalake.CustomerOrderProduct.CustomerOrderProductEntity;
import com.adg.api.department.Accounting.dal.entity.datalake.CustomerOrderProduct.CustomerOrderProductId;
import com.adg.api.department.Accounting.enums.MisaModel;
import com.adg.api.department.Accounting.mapper.CustomerOrderProductMapper;
import com.adg.api.department.Accounting.repository.customer_order_product.CustomerOrderProductRepository;
import com.adg.api.department.Accounting.service.AbstractMisaService;
import com.adg.api.department.Accounting.service.Order.OrderService;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.06.18 06:18
 */
@Service
@Log4j2
public class CustomerOrderProductService extends AbstractMisaService<CustomerOrderProductDTO, CustomerOrderProductEntity, CustomerOrderProductId, CustomerOrderProductMapper, CustomerOrderProductRepository> {

    @Autowired
    private OrderService orderService;

    public CustomerOrderProductService(CustomerOrderProductMapper mapper, CustomerOrderProductRepository repository) {
        super(mapper, repository);
    }

    @Override
    protected MisaModel getMisaModel() {
        return MisaModel.CUSTOMER_ORDER_PRODUCT;
    }

    @Override
    protected Map<String, Object> fetch() {
        long t1 = System.currentTimeMillis();
        log.info("Get custom table: START");
        List<CustomerOrderProductDTO> dtos = this.orderService.getCustomTable();
        log.info(String.format("Get custom table: END. Duration: %s. Size: %s", DateTimeUtils.getRunningTimeInSecond(t1), dtos.size()));
        long t2 = System.currentTimeMillis();
        log.info("Save custom table: START");
        List<CustomerOrderProductEntity> entities = dtos.stream().map(this.mapper::toEntity).collect(Collectors.toList());
        List<CustomerOrderProductEntity> savedEntities = this.repository.saveAll(entities);
        log.info(String.format("Save custom table: END. Duration: %s. Size: %s", DateTimeUtils.getRunningTimeInSecond(t1), savedEntities.size()));
        long t3 = System.currentTimeMillis();

        return MapUtils.ImmutableMap()
                .put("totalRecords", dtos.size())
                .put("totalPages", 1)
                .put("stats", List.of(MapUtils.ImmutableMap()
                                .put("recordCounts", entities.size())
                                .put("savedRecordCounts", savedEntities.size())
                                .put("page", 1)
                                .put("apiCallingDuration", t2 - t1)
                                .put("storingTime", t3 - t2)
                        .build()))
                .build();

    }
}
