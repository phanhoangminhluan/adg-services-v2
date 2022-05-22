package com.adg.api.department.Accounting.dal.entity.order;

import com.adg.api.department.Accounting.dal.entity.order_product.OrderProductDTO;
import com.adg.api.department.Accounting.service.AbstractDTO;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.11 15:51
 */
@Data
public class OrderDTO extends AbstractDTO {

    @SerializedName("async_id")
    private String asyncId;

    @SerializedName("form_layout_id")
    private int formLayoutId;

    @SerializedName("form_layout_id_text")
    private String formLayoutIdText;

    @SerializedName("owner_id")
    private int ownerId;

    @SerializedName("owner_id_text")
    private String ownerIdText;

    @SerializedName("sale_order_no")
    private String saleOrderNo;

    @SerializedName("sale_order_name")
    private String saleOrderName;

    @SerializedName("sale_order_date")
    private String saleOrderDate;

    @SerializedName("book_date")
    private String bookDate;

    @SerializedName("contract_parent_id")
    private int contractParentId;

    @SerializedName("account_id")
    private int accountId;

    @SerializedName("account_name")
    private String accountName;

    @SerializedName("contact_name")
    private String contactName;

    @SerializedName("sale_order_amount")
    private double saleOrderAmount;

    @SerializedName("deadline_date")
    private String deadlineDate;

    @SerializedName("revenue_status_id")
    private int revenueStatusId;

    @SerializedName("revenue_status_id_text")
    private String revenueStatusIdText;

    @SerializedName("recorded_sale")
    private double recordedSale;

    @SerializedName("status")
    private String status;

    @SerializedName("description")
    private String description;

    @SerializedName("is_use_currency")
    private boolean isUseCurrency;

    @SerializedName("currency_type")
    private String currencyType;

    @SerializedName("exchange_rate")
    private double exchangeRate;

    @SerializedName("total_receipted_amount")
    private double totalReceiptedAmount;

    @SerializedName("balance_receipt_amount")
    private double balanceReceiptAmount;

    @SerializedName("is_invoiced")
    private boolean isInvoiced;

    @SerializedName("invoiced_amount")
    private double invoicedAmount;

    @SerializedName("un_invoiced_amount")
    private double unInvoicedAmount;

    @SerializedName("un_subcrible")
    private boolean unsubscribe;

    @SerializedName("billing_country")
    private String billingCountry;

    @SerializedName("billing_province")
    private String billingProvince;

    @SerializedName("billing_address")
    private String billingAddress;

    @SerializedName("shipping_country")
    private String shippingCountry;

    @SerializedName("shipping_address")
    private String shippingAddress;

    @SerializedName("organization_unit_id")
    private int organizationUnitId;

    @SerializedName("organization_unit_name")
    private String organizationUnitName;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("modified_by")
    private String modifiedBy;

    @SerializedName("created_date")
    private String createdDate;

    @SerializedName("modified_date")
    private String modifiedDate;

    @SerializedName("is_public")
    private boolean isPublic;

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("total_summary")
    private double totalSummary;

    @SerializedName("tax_summary")
    private double taxSummary;

    @SerializedName("discount_summary")
    private double discountSummary;

    @SerializedName("to_currency_summary")
    private double toCurrencySummary;

    @SerializedName("is_sent_bill")
    private boolean isSentBill;

    @SerializedName("delivery_status")
    private String deliveryStatus;

    @SerializedName("pay_status")
    private String payStatus;

    @SerializedName("shipping_amount_summary")
    private double shippingAmountSummary;

    @SerializedName("is_contract_partner")
    private boolean isContractPartner;

    @SerializedName("amount_summary")
    private double amountSummary;

    @SerializedName("recorded_sale_users_id")
    private String recordedSaleUsersId;

    @SerializedName("recorded_sale_users_name")
    private String recordedSaleUsersName;

    @SerializedName("recorded_sale_organization_unit_id")
    private String recordedSaleOrganizationUnitId;

    @SerializedName("recorded_sale_organization_unit_name")
    private String recordedSaleOrganizationUnitName;

    @SerializedName("is_parent_sale_order")
    private boolean isParentSaleOrder;

    @SerializedName("to_currency_summary_oc")
    private double toCurrencySummaryOc;

    @SerializedName("discount_summary_oc")
    private double discountSummaryOc;

    @SerializedName("tax_summary_oc")
    private double taxSummaryOc;

    @SerializedName("total_summary_oc")
    private double totalSummaryOc;

    @SerializedName("sale_order_amount_oc")
    private double saleOrderAmountOc;

    @SerializedName("total_receipted_amount_oc")
    private double totalReceiptedAmountOc;

    @SerializedName("balance_receipt_amount_oc")
    private double balanceReceiptAmountOc;

    @SerializedName("invoiced_amount_oc")
    private double invoicedAmountOc;

    @SerializedName("opportunity_id")
    private String opportunityId;

    @SerializedName("quote_id")
    private String quoteId;

    @SerializedName("shipping_contact_id")
    private String shippingContactId;

    @SerializedName("sale_order_product_mappings")
    private List<OrderProductDTO> orderProducts = new ArrayList<>();

}