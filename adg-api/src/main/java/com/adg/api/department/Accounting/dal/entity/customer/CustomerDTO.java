package com.adg.api.department.Accounting.dal.entity.customer;

import com.adg.api.department.Accounting.service.AbstractDTO;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.11 15:47
 */
@Data
public class CustomerDTO extends AbstractDTO {

    @SerializedName("id")
    private int id;

    @SerializedName("form_layout_id")
    private int formLayoutId;

    @SerializedName("owner_id")
    private int ownerId;

    @SerializedName("owner_name")
    private String ownerName;

    @SerializedName("account_name")
    private String accountName;

    @SerializedName("account_short_name")
    private String accountShortName;

    @SerializedName("office_tel")
    private String officeTel;

    @SerializedName("office_email")
    private String officeEmail;

    @SerializedName("fax")
    private String fax;

    @SerializedName("website")
    private String website;

    @SerializedName("parent_account_name")
    private String parentAccountName;

    @SerializedName("account_number")
    private String accountNumber;

    @SerializedName("tax_code")
    private String taxCode;

    @SerializedName("budget_code")
    private String budgetCode;

    @SerializedName("bank_account")
    private String bankAccount;

    @SerializedName("bank_name")
    private String bankName;

    @SerializedName("description")
    private String description;

    @SerializedName("billing_address")
    private String billingAddress;

    @SerializedName("billing_country")
    private String billingCountry;

    @SerializedName("billing_province")
    private String billingProvince;

    @SerializedName("shipping_address")
    private String shippingAddress;

    @SerializedName("shipping_country")
    private String shippingCountry;

    @SerializedName("is_public")
    private boolean isPublic;

    @SerializedName("modified_date")
    private String modifiedDate;

    @SerializedName("modified_by")
    private String modifiedBy;

    @SerializedName("created_date")
    private String createdDate;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("async_id")
    private String asyncId;

    @SerializedName("organization_unit_id")
    private int organizationUnitId;

    @SerializedName("organization_unit_name")
    private String organizationUnitName;

}
