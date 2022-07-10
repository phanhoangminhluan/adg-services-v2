package com.adg.api.department.InternationalPayment.inventory.service;

import com.adg.api.department.InternationalPayment.inventory.dto.DonMuaHangDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.inventory.GetOrderByPortDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.inventory.OrderDTO;
import com.adg.api.department.InternationalPayment.inventory.entity.Bank;
import com.adg.api.department.InternationalPayment.inventory.entity.Order;
import com.adg.api.department.InternationalPayment.inventory.entity.Storage;
import com.adg.api.department.InternationalPayment.inventory.repository.CrmOrderRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:50
 */
@Service
public class CrmOrderService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CrmOrderRepository repository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private BankService bankService;

    @Autowired
    private OrderTransactionService orderTransactionService;

    @SneakyThrows
    @Transactional
    public List<Order> insertDonMuaHangRecord(Map<String, Object> donMuaHangRequest) {

        List<Map<String, Object>> donMuaHang = MapUtils.getListMapStringObject(donMuaHangRequest, "data.dmh");
        String portName = MapUtils.getString(donMuaHangRequest, "data.port");
        List<DonMuaHangDTO> donMuaHangDTOs = this.objectMapper.readValue(JsonUtils.toJson(donMuaHang), new TypeReference<>() {});

        List<Order> orders = donMuaHangDTOs.stream().map(donMuaHangDTO -> {
            Bank bank = this.bankService.findByName(donMuaHangDTO.getNganHangMoLC());
            Storage port = this.storageService.findByName(portName);
            return Order.newInstance(donMuaHangDTO, bank, port);
        }).collect(Collectors.toList());

        return this.repository.saveAll(orders);
    }

    public GetOrderByPortDTO getOrderByPort(String port, int pageIndex, int pageSize) {
        Page<Order> orderPage = this.repository.getOrdersByPortName(port, PageRequest.of(pageIndex, pageSize));
        List<OrderDTO> orderDTOs = new ArrayList<>();

        for (Order order : orderPage.getContent()) {
            double totalReleaseQuantity = this.orderTransactionService.countReleaseQuantityByOrderId(order.getId());
            orderDTOs.add(OrderDTO.newInstance(order, totalReleaseQuantity));
        }

        return GetOrderByPortDTO.builder()
                .orders(orderDTOs)
                .totalRecords(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .build();
    }

}
