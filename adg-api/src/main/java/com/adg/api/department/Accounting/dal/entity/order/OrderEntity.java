package com.adg.api.department.Accounting.dal.entity.order;

import com.adg.api.department.Accounting.dal.entity.BaseEntity;
import com.adg.api.department.Accounting.dal.entity.order_product.OrderProductEntity;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.mapper.annotations.MappingField;
import com.merlin.mapper.annotations.SourceFieldConfig;
import com.merlin.mapper.annotations.TargetFieldConfig;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.11 15:51
 */
//@Data
@Entity
@Getter
@Setter
@Table(name = "order", schema = "public")
public class OrderEntity extends BaseEntity {


    @Id
    @Column(name = "async_id")
    @MappingField
    private String asyncId;

    @Column
    @MappingField
    private int formLayoutId;

    @Column
    @MappingField
    private String formLayoutIdText;

    @Column
    @MappingField
    private int ownerId;

    @Column
    @MappingField
    private String ownerIdText;

    @Column
    @MappingField
    private String saleOrderNo;

    @Column
    @MappingField
    private String saleOrderName;

    @Column
    @MappingField(targetFieldConfigs = {
            @TargetFieldConfig(targetSetterMethod = "formatThenSetSaleOrderDate")
    })
    private ZonedDateTime saleOrderDate;

    @Column
    @MappingField(targetFieldConfigs = {
            @TargetFieldConfig(targetSetterMethod = "formatThenSetBookDate")
    })
    private ZonedDateTime bookDate;

    @Column
    @MappingField
    private int contractParentId;

    @Column
    @MappingField
    private int accountId;

    @Column
    @MappingField
    private String accountName;

    @Column
    @MappingField
    private String contactName;

    @Column
    @MappingField
    private double saleOrderAmount;

    @Column
    @MappingField(targetFieldConfigs = {
            @TargetFieldConfig(targetSetterMethod = "formatThenSetDeadlineDate")
    })
    private ZonedDateTime deadlineDate;

    @Column
    @MappingField
    private int revenueStatusId;

    @Column
    @MappingField
    private String revenueStatusIdText;

    @Column
    @MappingField
    private double recordedSale;

    @Column
    @MappingField
    private String status;

    @Column
    @MappingField
    @Type(type = "text")
    private String description;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setUseCurrency")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isUseCurrency")
            }
    )
    private boolean isUseCurrency;

    @Column
    @MappingField
    private String currencyType;

    @Column
    @MappingField
    private double exchangeRate;

    @Column
    @MappingField
    private double totalReceiptedAmount;

    @Column
    @MappingField
    private double balanceReceiptAmount;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setInvoiced")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isInvoiced")
            }
    )
    private boolean isInvoiced;

    @Column
    @MappingField
    private double invoicedAmount;

    @Column
    @MappingField
    private double unInvoicedAmount;

    @Column
    @MappingField
    private boolean unsubscribe;

    @Column
    @MappingField
    private String billingCountry;

    @Column
    @MappingField
    private String billingProvince;

    @Column
    @MappingField
    @Type(type = "text")
    private String billingAddress;

    @Column
    @MappingField
    private String shippingCountry;

    @Column
    @MappingField
    @Type(type = "text")
    private String shippingAddress;

    @Column
    @MappingField
    private int organizationUnitId;

    @Column
    @MappingField
    private String organizationUnitName;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setPublic")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isPublic")
            }
    )
    private boolean isPublic;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setDeleted")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isDeleted")
            }
    )
    private boolean isDeleted;

    @Column
    @MappingField
    private double totalSummary;

    @Column
    @MappingField
    private double taxSummary;

    @Column
    @MappingField
    private double discountSummary;

    @Column
    @MappingField
    private double toCurrencySummary;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setSentBill")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isSentBill")
            }
    )
    private boolean isSentBill;

    @Column
    @MappingField
    private String deliveryStatus;

    @Column
    @MappingField
    private String payStatus;

    @Column
    @MappingField
    private double shippingAmountSummary;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setContractPartner")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isContractPartner")
            }
    )
    private boolean isContractPartner;

    @Column
    @MappingField
    private double amountSummary;

    @Column
    @MappingField
    private String recordedSaleUsersId;

    @Column
    @MappingField
    private String recordedSaleUsersName;

    @Column
    @MappingField
    private String recordedSaleOrganizationUnitId;

    @Column
    @MappingField
    private String recordedSaleOrganizationUnitName;

    @Column
    @MappingField(
            targetFieldConfigs = {
                    @TargetFieldConfig(targetSetterMethod = "setParentSaleOrder")
            },
            sourceFieldConfigs = {
                    @SourceFieldConfig(sourceGetterMethod = "isParentSaleOrder")
            }
    )
    private boolean isParentSaleOrder;

    @Column
    @MappingField
    private double toCurrencySummaryOc;

    @Column
    @MappingField
    private double discountSummaryOc;

    @Column
    @MappingField
    private double taxSummaryOc;

    @Column
    @MappingField
    private double totalSummaryOc;

    @Column
    @MappingField
    private double saleOrderAmountOc;

    @Column
    @MappingField
    private double totalReceiptedAmountOc;

    @Column
    @MappingField
    private double balanceReceiptAmountOc;

    @Column
    @MappingField
    private double invoicedAmountOc;

    @Column
    @MappingField
    private String opportunityId;

    @Column
    @MappingField
    private String quoteId;

    @Column
    @MappingField
    private String shippingContactId;

//    @Column
//    @OneToMany(
//            mappedBy = "orderEntity",
//            cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE}
//    )
    @MappingField(allowNestedMapping = true)
    @Transient
    private List<OrderProductEntity> orderProducts;

    public void formatThenSetSaleOrderDate(String rawValue) {
        this.saleOrderDate = DateTimeUtils.convertStringToZonedDateTime(rawValue, DateTimeUtils.ISO8601_DATE_TIME_FORMATTER,  "Asia/Ho_Chi_Minh", "Asia/Ho_Chi_Minh");
    }

    public void formatThenSetBookDate(String rawValue) {
        this.bookDate = DateTimeUtils.convertStringToZonedDateTime(rawValue, DateTimeUtils.ISO8601_DATE_TIME_FORMATTER,  "Asia/Ho_Chi_Minh", "Asia/Ho_Chi_Minh");
    }

    public void formatThenSetDeadlineDate(String rawValue) {
        this.deadlineDate = DateTimeUtils.convertStringToZonedDateTime(rawValue, DateTimeUtils.ISO8601_DATE_TIME_FORMATTER,  "Asia/Ho_Chi_Minh", "Asia/Ho_Chi_Minh");
    }
}