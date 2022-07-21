package com.adg.api.department.InternationalPayment.inventory.service;

import com.adg.api.department.InternationalPayment.inventory.dto.FilePurchaseOrderDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.OrderNoteDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.PurchaseOrderDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.ResponseDTO;
import com.adg.api.department.InternationalPayment.inventory.dto.inventory.GetOrderDTO;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.11 01:50
 */
@Service
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

    public GetOrderDTO getOrderByPort(String portId, int pageIndex, int pageSize) {
        Page<Order> orderPage = this.repository.getOrdersByPortId(UUID.fromString(portId), PageRequest.of(pageIndex, pageSize));
        List<OrderDTO> orderDTOs = new ArrayList<>();

        for (Order order : orderPage.getContent()) {
            double totalReleaseQuantity = this.orderTransactionService.countReleaseQuantityByOrderId(order.getId());
            orderDTOs.add(OrderDTO.newInstance(order, totalReleaseQuantity));
        }

        return GetOrderDTO.builder()
                .orders(orderDTOs)
                .totalRecords(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .build();
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Set<UUID>>> updateNote(List<OrderNoteDTO> orderNoteDTOS) {

        Map<UUID, String> orderNoteMap = new HashMap<>();
        for (OrderNoteDTO orderNoteDTO : orderNoteDTOS) {
            orderNoteMap.put(orderNoteDTO.getOrderId(), orderNoteDTO.getNote());
        }

        List<Order> orders = this.repository.findAllById(orderNoteMap.keySet());

        Set<UUID> availableIDs = orders.stream().map(Order::getId).collect(Collectors.toSet());

        if (orders.size() != orderNoteMap.keySet().size()) {
            Set<UUID> errorIds = orderNoteMap.keySet()
                    .stream()
                    .filter(orderId -> !availableIDs.contains(orderId))
                    .collect(Collectors.toSet());
            return this.responseWrapper.error(errorIds, String.format("There are %s order_id does not exist", errorIds.size()));
        }

        orders.forEach(order -> order.setNote(orderNoteMap.get(order.getId())));
        this.repository.saveAll(orders);

        return this.responseWrapper.ok(availableIDs, String.format("%s orders with note have been updated", availableIDs.size()));
    }

}
