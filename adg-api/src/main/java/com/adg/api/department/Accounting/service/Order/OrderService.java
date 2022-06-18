package com.adg.api.department.Accounting.service.Order;

import com.adg.api.department.Accounting.dal.entity.datalake.CustomerOrderProduct.CustomerOrderProductDTO;
import com.adg.api.department.Accounting.dal.entity.order.OrderDTO;
import com.adg.api.department.Accounting.dal.entity.order.OrderEntity;
import com.adg.api.department.Accounting.dal.entity.order_product.OrderProductEntity;
import com.adg.api.department.Accounting.enums.MisaModel;
import com.adg.api.department.Accounting.mapper.OrderMapper;
import com.adg.api.department.Accounting.model.MisaPayload;
import com.adg.api.department.Accounting.repository.order.OrderRepository;
import com.adg.api.department.Accounting.repository.order_product.OrderProductRepository;
import com.adg.api.department.Accounting.service.AbstractMisaService;
import com.google.gson.reflect.TypeToken;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.22 16:43
 */
@Service
public class OrderService extends AbstractMisaService<OrderDTO, OrderEntity, String, OrderMapper, OrderRepository> {

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    public OrderService(OrderMapper mapper, OrderRepository repository) {
        super(mapper, repository);
    }

    @Override
    protected MisaModel getMisaModel() {
        return MisaModel.ORDER;
    }

    @Override
    protected Map<String, Object> save(MisaPayload payload) {
        long t1 = System.currentTimeMillis();
        List<OrderDTO> dtos = this.parsePayload(payload);
        List<OrderEntity> entities = dtos.stream().map(dto -> this.mapper.toEntityWithId(dto)).collect(Collectors.toList());
        List<OrderEntity> savedEntities = this.repository.saveAll(entities);
        List<OrderProductEntity> orderProductEntities = new ArrayList<>();
        for (OrderEntity orderEntity : entities) {
            orderProductEntities.addAll(orderEntity.getOrderProducts());
        }
        this.orderProductRepository.saveAll(orderProductEntities);
        return MapUtils.ImmutableMap()
                .put("recordCounts", entities.size())
                .put("savedRecordCounts", savedEntities.size())
                .put("storingTime", (System.currentTimeMillis() - t1))
                .build();
    }

    public List<CustomerOrderProductDTO> getCustomTable() {
        List<Map<String, Object>> customTableListMap = this.repository.getCustomTable();
        List<Map<String, Object>> reCustomTableListMap = customTableListMap.stream().map(record -> {
            Date date = (Date) record.get("sale_order_date");
            ZonedDateTime zdt = date.toLocalDate().atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")) ;
            String str = DateTimeUtils.convertZonedDateTimeToFormat(zdt, "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_03);
            Map<String, Object> newRecords = new HashMap<>(record);
            newRecords.put("sale_order_date", str);
            return newRecords;
        }).collect(Collectors.toList());

        return JsonUtils.fromJson(JsonUtils.toJson(reCustomTableListMap), (new TypeToken<List<CustomerOrderProductDTO>>() {
        }).getType());
    }
}
