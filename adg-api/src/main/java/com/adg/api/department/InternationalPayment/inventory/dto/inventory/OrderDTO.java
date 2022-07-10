package com.adg.api.department.InternationalPayment.inventory.dto.inventory;

import java.util.UUID;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.07.10 23:58
 */
public class OrderDTO {

    private UUID id;
    private String contractId;
    private String productId;
    private double price;
    private int totalStockQuantity;
    private int totalReleaseEntity;
    private String lcDate;

}
