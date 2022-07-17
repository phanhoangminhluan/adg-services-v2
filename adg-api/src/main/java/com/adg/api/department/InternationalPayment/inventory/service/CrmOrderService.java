package com.adg.api.department.InternationalPayment.inventory.service;

import com.adg.api.department.InternationalPayment.inventory.dto.FilePurchaseOrderDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.PurchaseOrderDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.inventory.GetOrderByPortDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.inventory.OrderDTO;
import com.adg.api.department.InternationalPayment.inventory.entity.Bank;
import com.adg.api.department.InternationalPayment.inventory.entity.Order;
import com.adg.api.department.InternationalPayment.inventory.entity.Storage;
import com.adg.api.department.InternationalPayment.inventory.repository.CrmOrderRepository;
import com.adg.api.general.http.ResponseWrapper;
import com.merlin.asset.core.utils.DateTimeUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:50
 */
@Service
//@DependsOn(value = {"customFilter"})
public class CrmOrderService {

    @Autowired
    private CrmOrderRepository repository;

    @Autowired
    private ResponseWrapper responseWrapper;

    @Autowired
    private StorageService storageService;

    @Autowired
    private BankService bankService;

    @Autowired
    private OrderTransactionService orderTransactionService;

    @SneakyThrows
    @Transactional
    public ResponseEntity insertDonMuaHangRecord(FilePurchaseOrderDTO filePurchaseOrderDTO) {

        String portId = filePurchaseOrderDTO.getPortId().toString();
        List<PurchaseOrderDTO> purchaseOrderDTOS = filePurchaseOrderDTO.getPurchaseOrders();

        List<Order> orders = purchaseOrderDTOS.stream().map(purchaseOrderDTO -> {
            Bank bank = this.bankService.findById(filePurchaseOrderDTO.getBankId());
            Storage port = this.storageService.findById(UUID.fromString(portId));
            return Order.newInstance(purchaseOrderDTO, bank, port);
        }).collect(Collectors.toList());
        List<String> errorMessages = new ArrayList<>();

        for (Order order : orders) {
            Optional<Order> orderOptional = this.repository.findByContractIdAndProductId(order.getContractId(), order.getProductId());
            if (!orderOptional.isEmpty()) {
                Order savedBeforeOrder = orderOptional.get();
                String errorMsg = String.format(
                        "This order (contract_id: '%s', product_id: '%s') had been inserted on '%s'.",
                        savedBeforeOrder.getContractId(),
                        savedBeforeOrder.getProductId(),
                        DateTimeUtils.convertZonedDateTimeToFormat(savedBeforeOrder.getUpdatedAt().atZone(ZoneId.of("Asia/Ho_Chi_Minh")), "Asia/Ho_Chi_Minh", DateTimeUtils.FMT_01)
                );
                errorMessages.add(errorMsg);
            }
        }
        if (!errorMessages.isEmpty()) {
            return this.responseWrapper.error(ResponseWrapper.NULL_DATA, errorMessages);
        }
        List<Order> savedOrders = this.repository.saveAll(orders);

        return this.responseWrapper.ok(ResponseWrapper.NULL_DATA, String.format("There are %s entities that were saved", savedOrders.size()));
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
