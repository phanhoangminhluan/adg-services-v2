package com.adg.api.department.Accounting.dal.entity.datalake.CustomerOrderProduct;

import com.adg.api.department.Accounting.dal.entity.BaseEntity;
import com.merlin.asset.core.utils.DateTimeUtils;
import com.merlin.mapper.annotations.MappingField;
import com.merlin.mapper.annotations.TargetFieldConfig;
import lombok.Data;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.06.13 23:21
 */
@Data
@Entity
@Table(name = "data_mart_customer_order_product", schema = "public")
@IdClass(CustomerOrderProductId.class)
public class CustomerOrderProductEntity extends BaseEntity implements Serializable {

    @Id
    @Nullable
    @MappingField
    @Column(columnDefinition = "text")
    private String customerId;
    @Id
    @Nullable
    @MappingField
    @Column(columnDefinition = "text")
    private String orderId;

    @Id
    @Nullable
    @MappingField
    @Column(columnDefinition = "text")
    private String productId;


    @MappingField(targetFieldConfigs = {
            @TargetFieldConfig(targetSetterMethod = "formatThenSetSaleOrderDate")
    })
    private ZonedDateTime saleOrderDate;

    @MappingField
    @Column(columnDefinition = "text")
    private String saleOrderNo;

    @MappingField
    @Column(columnDefinition = "text")
    private String status;

    @MappingField
    @Column(columnDefinition = "text")
    private String revenueStatusIdText;

    @MappingField
    @Column(columnDefinition = "text")
    private String productName;

    @MappingField
    private double price;

    @MappingField
    private double priceAfterTax;

    @MappingField
    private int amount;

    @MappingField
    private int shippingAmount;

    @MappingField
    private int total;

    @MappingField
    @Column(columnDefinition = "text")
    private String recordedSaleUsersName;

    @MappingField
    @Column(columnDefinition = "text")
    private String accountNumber;

    public void formatThenSetSaleOrderDate(String rawValue) {
        this.saleOrderDate =
                DateTimeUtils.convertStringToZonedDateTime(rawValue, DateTimeUtils.getFormatterWithDefaultValue(DateTimeUtils.FMT_03),
                        "Asia/Ho_Chi_Minh",
                        "Asia/Ho_Chi_Minh");
    }

}
