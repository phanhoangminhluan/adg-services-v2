package com.adg.api.department.Accounting.dal.entity.product;

import com.adg.api.department.Accounting.service.AbstractDTO;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.11 15:56
 */
@Data
public class ProductDTO extends AbstractDTO {

    @SerializedName("async_id")
    private String asyncId;

    @SerializedName("id")
    private int id;

    @SerializedName("conversion_rate")
    private double conversionRate;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("created_date")
    private String createdDate;

    @SerializedName("description")
    private String description;

    @SerializedName("form_layout_id")
    private int formLayoutId;

    @SerializedName("inactive")
    private boolean inactive;

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("is_follow_serial_number")
    private boolean isFollowSerialNumber;

    @SerializedName("is_public")
    private boolean isPublic;

    @SerializedName("is_use_tax")
    private boolean isUseTax;

    @SerializedName("modified_by")
    private String modifiedBy;

    @SerializedName("modified_date")
    private String modifiedDate;

    @SerializedName("organization_unit_id")
    private int organizationUnitId;

    @SerializedName("owner_id")
    private int ownerId;

    @SerializedName("owner_name")
    private String ownerName;

    @SerializedName("price_after_tax")
    private boolean priceAfterTax;

    @SerializedName("product_category_name")
    private String productCategoryName;

    @SerializedName("product_code")
    private String productCode;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("purchased_price")
    private double purchasedPrice;

    @SerializedName("quantity_demanded")
    private double quantityDemanded;

    @SerializedName("quantity_instock")
    private double quantityInstock;

    @SerializedName("quantity_ordered")
    private double quantityOrdered;

    @SerializedName("taxable")
    private boolean taxable;

    @SerializedName("tax_id_text")
    private String taxIdText;

    @SerializedName("unit_cost")
    private double unitCost;

    @SerializedName("unit_price")
    private double unitPrice;

    @SerializedName("unit_price1")
    private double unitPrice1;

    @SerializedName("unit_price2")
    private double unitPrice2;

    @SerializedName("unit_price_fixed")
    private double unitPriceFixed;

    @SerializedName("usage_unit_id_text")
    private String usageUnitIdText;

    @SerializedName("vendor_name_id")
    private int vendorNameId;

}
