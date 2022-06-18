package com.adg.api.department.Accounting.dal.entity.datalake.CustomerOrderProduct;

import com.adg.api.department.Accounting.service.AbstractDTO;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.06.13 23:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderProductDTO extends AbstractDTO {

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("sale_order_date")
    private String saleOrderDate;

    @SerializedName("sale_order_no")
    private String saleOrderNo;

    @SerializedName("status")
    private String status;

    @SerializedName("revenue_status_id_text")
    private String revenueStatusIdText;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("price")
    private double price;

    @SerializedName("price_after_tax")
    private double priceAfterTax;

    @SerializedName("amount")
    private double amount;

    @SerializedName("shipping_amount")
    private double shippingAmount;

    @SerializedName("total")
    private double total;

    @SerializedName("recorded_sale_users_name")
    private String recordedSaleUsersName;

    @SerializedName("account_number")
    private String accountNumber;

    @SerializedName("modified_by")
    private String modifiedBy;

    @SerializedName("created_date")
    private String createdDate;

    @SerializedName("modified_date")
    private String modifiedDate;

    @SerializedName("created_by")
    private String createdBy;

}
