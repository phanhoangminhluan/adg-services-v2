package com.adg.api.department.Accounting.dal.entity.order_product;

import com.adg.api.department.Accounting.service.AbstractDTO;
import com.google.gson.annotations.SerializedName;
import com.merlin.asset.core.utils.ParserUtils;
import lombok.Data;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.12 15:22
 */
@Data
public class OrderProductDTO extends AbstractDTO {

    @SerializedName("product_id")
    private int productId;

    @SerializedName("unit")
    private String unit;

    @SerializedName("price")
    private double price;

    @SerializedName("amount")
    private double amount;

    @SerializedName("to_currency")
    private double toCurrency;

    @SerializedName("discount")
    private double discount;

    @SerializedName("tax")
    private double tax;

    @SerializedName("total")
    private double total;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("product_code")
    private String productCode;

    @SerializedName("created_date")
    private String createdDate;

    @SerializedName("modified_date")
    private String modifiedDate;

    @SerializedName("async_id")
    private String asyncId;

    @SerializedName("tax_percent")
    private String taxPercent;

    @SerializedName("discount_percent")
    private double discountPercent;

    @SerializedName("shipping_amount")
    private double shippingAmount;

    @SerializedName("usage_unit_amount")
    private double usageUnitAmount;

    @SerializedName("description")
    private String description;

    @SerializedName("to_currency_oc")
    private double toCurrencyOc;

    @SerializedName("discount_oc")
    private double discountOc;

    @SerializedName("tax_oc")
    private double taxOc;

    @SerializedName("total_oc")
    private double totalOc;

    @SerializedName("is_discount_directly")
    private boolean isDiscountDirectly;

    @SerializedName("price_after_tax")
    private double priceAfterTax;

    @SerializedName("sort_order")
    private int sortOrder;

    private String createdBy;

    private String modifiedBy;

    public void setCreatedBy(String createdBy) {
        if (ParserUtils.isNullOrEmpty(createdBy)) {
            this.createdBy = "default";
        } else {
            this.createdBy = createdBy;
        }
    }

    public void setModifiedBy(String modifiedBy) {
        if (ParserUtils.isNullOrEmpty(modifiedBy)) {
            this.modifiedBy = "default";
        } else {
            this.modifiedBy = modifiedBy;
        }
    }

    public String getCreatedBy() {
        if (ParserUtils.isNullOrEmpty(createdBy)) {
            this.createdBy = "default";
        }
        return this.createdBy;
    }

    public String getModifiedBy() {
        if (ParserUtils.isNullOrEmpty(modifiedBy)) {
            this.modifiedBy = "default";
        }
        return this.modifiedBy;
    }
}
