package com.adg.api.department.InternationalPayment.inventory.dto.inventory;

import com.adg.api.department.InternationalPayment.inventory.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.10 23:58
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderDTO {

    private UUID id;
    private String contractId;
    private String productId;
    private double price;
    private double totalStockQuantity;
    private double totalReleaseQuantity;
    private LocalDate lcDate;

    public static OrderDTO newInstance(Order order, double totalReleaseQuantity) {
        return OrderDTO.builder()
                .id(order.getId())
                .contractId(order.getContractId())
                .productId(order.getProductId())
                .price(order.getPrice())
                .totalStockQuantity(order.getReceivedQuantity() - totalReleaseQuantity)
                .totalReleaseQuantity(totalReleaseQuantity)
                .lcDate(order.getLcDate().toLocalDate())
                .build();
    }

}
